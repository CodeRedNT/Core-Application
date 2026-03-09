package br.com.coderednt.coreapp.features.performance.internal

import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.logging.Logger
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
 * Esta classe utiliza o módulo de logging estruturado para telemetria, garantindo 
 * que erros internos do SDK sejam registrados sem causar recursão infinita.
 */
@Singleton
class AppHealthTrackerImpl @Inject constructor(
    private val analyticsTracker: AnalyticsTracker,
    private val initializers: Map<Class<out ModuleInitializer>, @JvmSuppressWildcards Provider<ModuleInitializer>>,
    private val logger: Logger
) : AppHealthTracker {

    private val _metrics = MutableStateFlow(HealthMetrics())
    override val metrics: StateFlow<HealthMetrics> = _metrics.asStateFlow()

    private val initializedModules = Collections.synchronizedSet(mutableSetOf<String>())
    private val initializationStack = Collections.synchronizedList(mutableListOf<String>())

    override fun onAppStart() {
        AppStartupTracker.markAppStart()
        logger.i("Aplicação iniciada. Iniciando rastreamento de saúde...")
    }

    override fun onAppEnd() {
        AppStartupTracker.markAppEnd()
        val runtime = Runtime.getRuntime()
        val initialUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
        
        _metrics.update { 
            it.copy(memory = it.memory.copy(initialMemoryMb = initialUsed))
        }
        logger.i("Fim do onCreate da Application. Memória inicial: %.2f MB", initialUsed)
    }

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
        logger.d("Startup consolidado: OS Overhead=%.2fms, Providers=%.2fms, App=%.2fms", osOverhead, providerInit, appInit)
    }

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

    override fun trackRenderTime(screenName: String, timeMillis: Long) {
        _metrics.update { 
            it.copy(ui = it.ui.copy(renderTimes = it.ui.renderTimes + (screenName to timeMillis)))
        }
        logger.d("Renderização da tela [%s]: %d ms", screenName, timeMillis)
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
        logger.w("Frame lento (Jank) detectado na tela [%s]", screenName)
    }

    override fun trackRecomposition(composableName: String) {
        _metrics.update { 
            val current = it.ui.recompositionCounts[composableName] ?: 0
            it.copy(ui = it.ui.copy(recompositionCounts = it.ui.recompositionCounts + (composableName to (current + 1))))
        }
    }

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
        logger.d("Garbage Collection detectado pelo sentinela.")
    }

    override fun trackBattery(metrics: BatteryMetrics) {
        _metrics.update { 
            val initialLevel = if (it.battery.initialLevel == -1) metrics.level else it.battery.initialLevel
            it.copy(battery = metrics.copy(initialLevel = initialLevel))
        }
    }

    override fun trackApiLatency(endpoint: String, durationMs: Long) {
        _metrics.update { 
            it.copy(ui = it.ui.copy(apiLatencies = it.ui.apiLatencies + (endpoint to durationMs)))
        }
    }

    override fun trackError(message: String) {
        _metrics.update { it.copy(lastError = message) }
        // Utilizamos o método 'e' que apenas loga, evitando o loop infinito com 'logAndTrack'
        logger.e(message = "Erro de Saúde do App: %s", args = arrayOf(message))
    }

    override fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean) {
        val provider = initializers[clazz]
        if (provider != null) {
            loadModule(provider.get(), isParallel)
        } else {
            val errorMsg = "Inicializador não configurado no Hilt: ${clazz.simpleName}"
            trackError(errorMsg)
        }
    }

    override fun loadModule(initializer: ModuleInitializer, isParallel: Boolean) {
        val moduleName = initializer.name
        
        if (initializedModules.contains(moduleName)) return

        if (initializationStack.contains(moduleName)) {
            val cycle = initializationStack.joinToString(" -> ") + " -> $moduleName"
            trackError("DEPENDÊNCIA CIRCULAR: $cycle")
            return
        }

        initializationStack.add(moduleName)
        val start = TimeUtils.nowNanos()
        try {
            logger.d("Iniciando módulo: %s", moduleName)
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
            logger.i("Módulo [%s] inicializado em %.2f ms", moduleName, durationMs)
        } catch (e: Exception) {
            val errorMsg = "Falha no modulo $moduleName: ${e.message}"
            trackError(errorMsg)
            // Registra a exceção real sem causar recursão
            logger.e(e, errorMsg)
        } finally {
            initializationStack.remove(moduleName)
        }
    }
}
