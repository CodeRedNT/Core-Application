package br.com.coderednt.coreapp.core.monitoring.performance

import kotlinx.coroutines.flow.StateFlow

/**
 * Representa as métricas capturadas durante o processo de inicialização do aplicativo.
 * 
 * @property osOverheadTimeMs Tempo decorrido entre o início do processo e a primeira execução de código do app.
 * @property providerInitTimeMs Tempo gasto na inicialização de ContentProviders.
 * @property diInitializationTimeMs Tempo gasto na configuração do framework de Injeção de Dependência (Hilt/Dagger).
 * @property appOnCreateTimeMs Tempo total de execução do método onCreate da classe Application.
 * @property activityLaunchDelayMs Tempo entre o final da Application e o início da primeira Activity.
 * @property uiInflationTimeMs Tempo gasto para inflar a primeira tela (Compose/XML).
 * @property splashScreenDurationMs Duração da exibição da Splash Screen nativa.
 * @property moduleLoadTimes Mapa de módulos carregados de forma síncrona e seus respectivos tempos.
 * @property parallelModuleLoadTimes Mapa de módulos carregados de forma assíncrona.
 */
data class StartupMetrics(
    val osOverheadTimeMs: Double = 0.0,
    val providerInitTimeMs: Double = 0.0,
    val diInitializationTimeMs: Double = 0.0,
    val appOnCreateTimeMs: Double = 0.0,
    val activityLaunchDelayMs: Double = 0.0,
    val uiInflationTimeMs: Double = 0.0,
    val splashScreenDurationMs: Double = 0.0,
    val moduleLoadTimes: Map<String, Double> = emptyMap(),
    val parallelModuleLoadTimes: Map<String, Double> = emptyMap()
) {
    /**
     * Calcula o tempo total de startup somando todas as fases rastreadas.
     */
    val totalStartupTimeMs: Double 
        get() = osOverheadTimeMs + providerInitTimeMs + diInitializationTimeMs + 
                appOnCreateTimeMs + moduleLoadTimes.values.sum() + 
                activityLaunchDelayMs + uiInflationTimeMs + splashScreenDurationMs
}

/**
 * Métricas relacionadas à fluidez da interface e interações do usuário.
 */
data class UIMetrics(
    val renderTimes: Map<String, Long> = emptyMap(),
    val navigationTimes: Map<String, Long> = emptyMap(),
    val jankCounts: Map<String, Int> = emptyMap(),
    val apiLatencies: Map<String, Long> = emptyMap(),
    val recompositionCounts: Map<String, Int> = emptyMap()
)

/**
 * Métricas de utilização de memória do dispositivo e do processo.
 */
data class MemoryMetrics(
    val initialMemoryMb: Double = 0.0,
    val usedHeapMb: Double = 0.0,
    val totalHeapMb: Double = 0.0,
    val availableSystemMemGb: Double = 0.0,
    val isLowMemory: Boolean = false,
    val gcCount: Int = 0,
    val activityMemoryUsage: Map<String, Double> = emptyMap()
)

/**
 * Estado atual e histórico de consumo da bateria.
 */
data class BatteryMetrics(
    val initialLevel: Int = -1,
    val level: Int = -1,
    val isCharging: Boolean = false,
    val temperature: Float = 0f,
    val health: String = "Unknown",
    val currentNowMa: Int = 0
) {
    /**
     * Retorna a porcentagem de bateria consumida desde a inicialização do rastreamento.
     */
    val dropPercentage: Int
        get() = if (initialLevel > 0 && level > 0 && initialLevel > level) initialLevel - level else 0
}

/**
 * Modelo agregado contendo todo o estado de saúde e performance do aplicativo.
 */
data class HealthMetrics(
    val startup: StartupMetrics = StartupMetrics(),
    val ui: UIMetrics = UIMetrics(),
    val memory: MemoryMetrics = MemoryMetrics(),
    val battery: BatteryMetrics = BatteryMetrics(),
    val lastError: String? = null
)

/**
 * Define o contrato para monitoramento de memória.
 */
interface MemoryTracker {
    fun trackMemory(metrics: MemoryMetrics)
    fun trackActivityMemory(activityName: String, usedMb: Double)
    fun notifyGC()
}

/**
 * Define o contrato para monitoramento de energia/bateria.
 */
interface BatteryTracker {
    fun trackBattery(metrics: BatteryMetrics)
}

/**
 * Define o contrato para monitoramento de renderização e navegação.
 */
interface UITracker {
    fun trackRenderTime(screenName: String, timeMillis: Long)
    fun trackNavigationTime(route: String, durationMs: Long)
    fun trackJank(screenName: String)
    fun trackRecomposition(composableName: String)
}

/**
 * Interface mestre para o rastreamento global de saúde da aplicação.
 * 
 * Centraliza o acesso ao estado de [HealthMetrics] e coordena os diferentes rastreadores.
 */
interface AppHealthTracker : MemoryTracker, BatteryTracker, UITracker {
    
    /**
     * Fluxo de estado contínuo das métricas consolidadas.
     */
    val metrics: StateFlow<HealthMetrics>
    
    fun onAppStart()
    fun onAppEnd()

    /**
     * Registra o tempo de uma fase específica do ciclo de vida.
     */
    fun trackPhaseTime(phase: StartupPhase, durationMs: Double)
    
    /**
     * Registra a latência de uma chamada de rede para análise de performance.
     */
    fun trackApiLatency(endpoint: String, durationMs: Long)
    
    /**
     * Registra uma mensagem de erro ou stacktrace capturado globalmente.
     */
    fun trackError(message: String)
    
    /**
     * Carrega e monitora a inicialização de um módulo específico.
     */
    fun loadModule(initializer: ModuleInitializer, isParallel: Boolean = false)
    
    /**
     * Carrega e monitora a inicialização de um módulo via reflexão/Hilt.
     */
    fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean = false)
    
    /**
     * Finaliza e consolida o rastreamento de startup.
     */
    fun trackAppStartup()
}

/**
 * Fases conhecidas do ciclo de vida de inicialização.
 */
enum class StartupPhase {
    OS_OVERHEAD, PROVIDER_INIT, DI_INIT, APP_ONCREATE, ACTIVITY_LAUNCH, UI_INFLATION, SPLASH_SCREEN
}

/**
 * Escopo utilitário para carregamento fluente de módulos.
 */
class ModuleLoaderScope(val tracker: AppHealthTracker, val isParallel: Boolean) {
    inline fun <reified T : ModuleInitializer> module() {
        tracker.load(T::class.java, isParallel)
    }
}

/**
 * DSL para carregamento síncrono de módulos.
 */
inline fun AppHealthTracker.sync(block: ModuleLoaderScope.() -> Unit) {
    ModuleLoaderScope(this, isParallel = false).block()
}

/**
 * DSL para carregamento paralelo/assíncrono de módulos.
 */
inline fun AppHealthTracker.async(block: ModuleLoaderScope.() -> Unit) {
    ModuleLoaderScope(this, isParallel = true).block()
}
