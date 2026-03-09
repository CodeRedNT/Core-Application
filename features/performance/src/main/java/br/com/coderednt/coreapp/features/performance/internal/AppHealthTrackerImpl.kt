package br.com.coderednt.coreapp.features.performance.internal

import android.util.Log
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.analytics.AnalyticsTracker
import br.com.coderednt.coreapp.core.monitoring.performance.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.Collections
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * Implementação orquestradora do [AppHealthTracker] com suporte a Safe Initializers.
 * 
 * Esta classe centraliza o estado de todas as métricas de saúde do aplicativo 
 * (Startup, UI, Memória, Bateria) e gerencia o ciclo de vida de inicialização 
 * dos módulos, prevenindo dependências circulares e re-inicializações.
 * 
 * @property analyticsTracker Rastreador para reportar eventos críticos e erros.
 * @property initializers Mapa de provedores de inicialização injetados via Hilt Multibinding.
 */
@Singleton
class AppHealthTrackerImpl @Inject constructor(
    private val analyticsTracker: AnalyticsTracker,
    private val initializers: Map<Class<out ModuleInitializer>, @JvmSuppressWildcards Provider<ModuleInitializer>>
) : AppHealthTracker {

    private val _metrics = MutableStateFlow(HealthMetrics())
    
    /**
     * Fluxo de estado contínuo das métricas, acessível por ViewModels para renderização na UI.
     */
    override val metrics: StateFlow<HealthMetrics> = _metrics.asStateFlow()

    // Controle de Safe Initializers
    private val initializedModules = Collections.synchronizedSet(mutableSetOf<String>())
    private val initializationStack = Collections.synchronizedList(mutableListOf<String>())

    // --- Métricas de Ciclo de Vida do App ---

    /**
     * Marca o início do ciclo de vida da Application.
     */
    override fun onAppStart() {
        AppStartupTracker.markAppStart()
    }

    /**
     * Marca o fim da fase de inicialização da Application e captura a memória base inicial.
     */
    override fun onAppEnd() {
        AppStartupTracker.markAppEnd()
        val runtime = Runtime.getRuntime()
        val initialUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
        
        _metrics.update { 
            it.copy(memory = it.memory.copy(initialMemoryMb = initialUsed))
        }
    }

    /**
     * Consolida as métricas de startup (OS Overhead, Provider Init, App Init).
     */
    override fun trackAppStartup() {
        val osOverhead = TimeUtils.nanosToMillis(AppStartupTracker.providerStartTimeNanos - AppStartupTracker.processStartTimeNanos)
        val providerInit = TimeUtils.nanosToMillis(AppStartupTracker.appStartTimeNanos - AppStartupTracker.providerStartTimeNanos)
        val appInit = TimeUtils.nanosToMillis(AppStartupTracker.appEndTimeNanos - AppStartupTracker.appStartTimeNanos)
        
        _metrics.update { 
            val updatedStartup = it.startup.copy(
                osOverheadTimeMs = if (osOverhead > 0) osOverhead else it.startup.osOverheadTimeMs,
                providerInitTimeMs = if (providerInit > 0) providerInit else it.startup.providerInitTimeMs,
                appOnCreateTimeMs = if (appInit > 0) appInit else it.startup.appOnCreateTimeMs
            )
            it.copy(startup = updatedStartup)
        }
    }

    /**
     * Registra o tempo de duração de uma fase específica do startup.
     */
    override fun trackPhaseTime(phase: StartupPhase, durationMs: Double) {
        _metrics.update { 
            val updatedStartup = when(phase) {
                StartupPhase.DI_INIT -> it.startup.copy(diInitializationTimeMs = durationMs)
                StartupPhase.UI_INFLATION -> it.startup.copy(uiInflationTimeMs = durationMs)
                StartupPhase.OS_OVERHEAD -> it.startup.copy(osOverheadTimeMs = durationMs)
                StartupPhase.PROVIDER_INIT -> it.startup.copy(providerInitTimeMs = durationMs)
                StartupPhase.APP_ONCREATE -> it.startup.copy(appOnCreateTimeMs = durationMs)
                StartupPhase.ACTIVITY_LAUNCH -> it.startup.copy(activityLaunchDelayMs = durationMs)
                StartupPhase.SPLASH_SCREEN -> it.startup.copy(splashScreenDurationMs = durationMs)
            }
            it.copy(startup = updatedStartup)
        }
    }

    // --- Delegação de UI (UITracker) ---

    override fun trackRenderTime(screenName: String, timeMillis: Long) {
        _metrics.update { 
            it.copy(ui = it.ui.copy(renderTimes = it.ui.renderTimes + (screenName to timeMillis)))
        }
    }

    override fun trackNavigationTime(route: String, durationMs: Long) {
        _metrics.update { 
            it.copy(ui = it.ui.copy(navigationTimes = it.ui.navigationTimes + (route to durationMs)))
        }
    }

    override fun trackJank(screenName: String) {
        _metrics.update { 
            val current = it.ui.jankCounts[screenName] ?: 0
            it.copy(ui = it.ui.copy(jankCounts = it.ui.jankCounts + (screenName to (current + 1))))
        }
    }

    override fun trackRecomposition(composableName: String) {
        _metrics.update { 
            val current = it.ui.recompositionCounts[composableName] ?: 0
            it.copy(ui = it.ui.copy(recompositionCounts = it.ui.recompositionCounts + (composableName to (current + 1))))
        }
    }

    // --- Delegação de Sistema (MemoryTracker & BatteryTracker) ---

    override fun trackMemory(metrics: MemoryMetrics) {
        _metrics.update { 
            it.copy(memory = metrics.copy(
                initialMemoryMb = it.memory.initialMemoryMb,
                activityMemoryUsage = it.memory.activityMemoryUsage,
                gcCount = it.memory.gcCount
            ))
        }
    }

    override fun trackActivityMemory(activityName: String, usedMb: Double) {
        _metrics.update { 
            val updatedActivityMap = it.memory.activityMemoryUsage + (activityName to usedMb)
            it.copy(memory = it.memory.copy(activityMemoryUsage = updatedActivityMap))
        }
    }

    override fun notifyGC() {
        _metrics.update { 
            it.copy(memory = it.memory.copy(gcCount = it.memory.gcCount + 1))
        }
    }

    override fun trackBattery(metrics: BatteryMetrics) {
        _metrics.update { 
            val initialLevel = if (it.battery.initialLevel == -1) metrics.level else it.battery.initialLevel
            it.copy(battery = metrics.copy(initialLevel = initialLevel))
        }
    }

    // --- Infraestrutura e Inicialização ---

    override fun trackApiLatency(endpoint: String, durationMs: Long) {
        _metrics.update { 
            it.copy(ui = it.ui.copy(apiLatencies = it.ui.apiLatencies + (endpoint to durationMs)))
        }
    }

    /**
     * Registra erros globais no estado das métricas.
     */
    override fun trackError(message: String) {
        _metrics.update { it.copy(lastError = message) }
    }

    /**
     * Carrega um inicializador de módulo via Hilt Provider.
     * 
     * @param clazz A classe do inicializador a ser buscada no mapa de injeção.
     * @param isParallel Se verdadeiro, o tempo de inicialização será registrado como paralelo.
     */
    override fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean) {
        val provider = initializers[clazz]
        if (provider != null) {
            loadModule(provider.get(), isParallel)
        } else {
            Log.e("AppHealthTracker", "ERRO CRÍTICO: Inicializador não configurado no Hilt: ${clazz.simpleName}")
            trackError("Falha de DI: Modulo ${clazz.simpleName} não mapeado.")
        }
    }

    /**
     * Executa a lógica de inicialização de um [ModuleInitializer].
     * 
     * Implementa proteção contra dependência circular e garante que cada módulo seja 
     * inicializado apenas uma vez no ciclo de vida do processo.
     */
    override fun loadModule(initializer: ModuleInitializer, isParallel: Boolean) {
        val moduleName = initializer.name
        
        if (initializedModules.contains(moduleName)) {
            Log.d("AppHealthTracker", "Modulo $moduleName já inicializado. Ignorando.")
            return
        }

        if (initializationStack.contains(moduleName)) {
            val cycle = initializationStack.joinToString(" -> ") + " -> $moduleName"
            Log.e("AppHealthTracker", "DEPENDÊNCIA CIRCULAR DETECTADA: $cycle")
            trackError("Erro de Inicialização: Ciclo detectado em $moduleName")
            return
        }

        initializationStack.add(moduleName)
        
        val start = TimeUtils.nowNanos()
        try {
            initializer.initialize()
            val durationMs = TimeUtils.calculateDurationFrom(start)
            
            initializedModules.add(moduleName)
            
            _metrics.update { 
                val updatedStartup = if (isParallel) {
                    it.startup.copy(parallelModuleLoadTimes = it.startup.parallelModuleLoadTimes + (moduleName to durationMs))
                } else {
                    it.startup.copy(moduleLoadTimes = it.startup.moduleLoadTimes + (moduleName to durationMs))
                }
                it.copy(startup = updatedStartup)
            }
        } catch (e: Exception) {
            Log.e("AppHealthTracker", "Falha ao inicializar modulo $moduleName", e)
            trackError("Falha no modulo $moduleName: ${e.message}")
        } finally {
            initializationStack.remove(moduleName)
        }
    }
}
