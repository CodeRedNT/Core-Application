package br.com.coderednt.coreapp.core.monitoring.performance

import kotlinx.coroutines.flow.StateFlow

/**
 * Métricas específicas do processo de inicialização (Startup).
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
    val totalStartupTimeMs: Double 
        get() = osOverheadTimeMs + providerInitTimeMs + diInitializationTimeMs + 
                appOnCreateTimeMs + moduleLoadTimes.values.sum() + 
                activityLaunchDelayMs + uiInflationTimeMs + splashScreenDurationMs
}

/**
 * Métricas de performance de UI e experiência do usuário.
 */
data class UIMetrics(
    val renderTimes: Map<String, Long> = emptyMap(),
    val navigationTimes: Map<String, Long> = emptyMap(),
    val jankCounts: Map<String, Int> = emptyMap(),
    val apiLatencies: Map<String, Long> = emptyMap(),
    val recompositionCounts: Map<String, Int> = emptyMap()
)

/**
 * Representa as métricas de memória do sistema.
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
 * Representa o estado da bateria do dispositivo.
 */
data class BatteryMetrics(
    val initialLevel: Int = -1,
    val level: Int = -1,
    val isCharging: Boolean = false,
    val temperature: Float = 0f,
    val health: String = "Unknown",
    val currentNowMa: Int = 0
) {
    val dropPercentage: Int
        get() = if (initialLevel > 0 && level > 0 && initialLevel > level) initialLevel - level else 0
}

/**
 * Modelo consolidado de todas as métricas de saúde do aplicativo.
 */
data class HealthMetrics(
    val startup: StartupMetrics = StartupMetrics(),
    val ui: UIMetrics = UIMetrics(),
    val memory: MemoryMetrics = MemoryMetrics(),
    val battery: BatteryMetrics = BatteryMetrics(),
    val lastError: String? = null
)

/**
 * Interface para rastreamento de memória.
 */
interface MemoryTracker {
    fun trackMemory(metrics: MemoryMetrics)
    fun trackActivityMemory(activityName: String, usedMb: Double)
    fun notifyGC()
}

/**
 * Interface para rastreamento de bateria.
 */
interface BatteryTracker {
    fun trackBattery(metrics: BatteryMetrics)
}

/**
 * Interface para rastreamento de performance de UI.
 */
interface UITracker {
    fun trackRenderTime(screenName: String, timeMillis: Long)
    fun trackNavigationTime(route: String, durationMs: Long)
    fun trackJank(screenName: String)
    fun trackRecomposition(composableName: String)
}

/**
 * Interface principal que coordena o monitoramento de saúde do app.
 */
interface AppHealthTracker : MemoryTracker, BatteryTracker, UITracker {
    val metrics: StateFlow<HealthMetrics>
    
    fun onAppStart()
    fun onAppEnd()

    fun trackPhaseTime(phase: StartupPhase, durationMs: Double)
    fun trackApiLatency(endpoint: String, durationMs: Long)
    
    fun trackError(message: String)
    fun loadModule(initializer: ModuleInitializer, isParallel: Boolean = false)
    fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean = false)
    fun trackAppStartup()
}

enum class StartupPhase {
    OS_OVERHEAD, PROVIDER_INIT, DI_INIT, APP_ONCREATE, ACTIVITY_LAUNCH, UI_INFLATION, SPLASH_SCREEN
}

class ModuleLoaderScope(val tracker: AppHealthTracker, val isParallel: Boolean) {
    inline fun <reified T : ModuleInitializer> module() {
        tracker.load(T::class.java, isParallel)
    }
}

inline fun AppHealthTracker.sync(block: ModuleLoaderScope.() -> Unit) {
    ModuleLoaderScope(this, isParallel = false).block()
}

inline fun AppHealthTracker.async(block: ModuleLoaderScope.() -> Unit) {
    ModuleLoaderScope(this, isParallel = true).block()
}
