package br.com.coderednt.coreapp.features.performance.internal

import br.com.coderednt.coreapp.core.monitoring.analytics.AnalyticsTracker
import br.com.coderednt.coreapp.core.monitoring.performance.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class AppHealthTrackerImpl @Inject constructor(
    private val analyticsTracker: AnalyticsTracker,
    private val initializers: Map<Class<*>, @JvmSuppressWildcards Provider<ModuleInitializer>>
) : AppHealthTracker {

    private val _metrics = MutableStateFlow(HealthMetrics())
    override val metrics: StateFlow<HealthMetrics> = _metrics.asStateFlow()

    override fun onAppStart() {
        AppStartupTracker.markAppStart()
    }

    override fun onAppEnd() {
        AppStartupTracker.markAppEnd()
        // Captura a memória logo após o fim do onCreate da Application
        val runtime = Runtime.getRuntime()
        val initialUsed = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
        _metrics.update { it.copy(memory = it.memory.copy(initialMemoryMb = initialUsed)) }
    }

    private fun refreshStartupMetrics() {
        val osOverhead = (AppStartupTracker.providerStartTimeNanos - AppStartupTracker.processStartTimeNanos) / 1_000_000.0
        val providerInit = (AppStartupTracker.appStartTimeNanos - AppStartupTracker.providerStartTimeNanos) / 1_000_000.0
        val appInit = (AppStartupTracker.appEndTimeNanos - AppStartupTracker.appStartTimeNanos) / 1_000_000.0
        
        _metrics.update { 
            it.copy(
                osOverheadTimeMs = if (osOverhead > 0) osOverhead else it.osOverheadTimeMs,
                providerInitTimeMs = if (providerInit > 0) providerInit else it.providerInitTimeMs,
                appOnCreateTimeMs = if (appInit > 0) appInit else it.appOnCreateTimeMs
            )
        }
    }
    
    override fun trackRenderTime(screenName: String, timeMillis: Long) {
        _metrics.update { it.copy(renderTimes = it.renderTimes + (screenName to timeMillis)) }
    }

    override fun trackNavigationTime(route: String, durationMs: Long) {
        _metrics.update { it.copy(navigationTimes = it.navigationTimes + (route to durationMs)) }
    }

    override fun trackPhaseTime(phase: StartupPhase, durationMs: Double) {
        _metrics.update { 
            when(phase) {
                StartupPhase.DI_INIT -> it.copy(diInitializationTimeMs = durationMs)
                StartupPhase.UI_INFLATION -> it.copy(uiInflationTimeMs = durationMs)
                StartupPhase.OS_OVERHEAD -> it.copy(osOverheadTimeMs = durationMs)
                StartupPhase.PROVIDER_INIT -> it.copy(providerInitTimeMs = durationMs)
                StartupPhase.APP_ONCREATE -> it.copy(appOnCreateTimeMs = durationMs)
                StartupPhase.ACTIVITY_LAUNCH -> it.copy(activityLaunchDelayMs = durationMs)
                StartupPhase.SPLASH_SCREEN -> it.copy(splashScreenDurationMs = durationMs)
            }
        }
    }

    override fun trackApiLatency(endpoint: String, durationMs: Long) {
        _metrics.update { it.copy(apiLatencies = it.apiLatencies + (endpoint to durationMs)) }
    }

    override fun trackJank(screenName: String) {
        _metrics.update { 
            val current = it.jankCounts[screenName] ?: 0
            it.copy(jankCounts = it.jankCounts + (screenName to (current + 1)))
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
    }

    override fun trackBattery(metrics: BatteryMetrics) {
        _metrics.update { it.copy(battery = metrics) }
    }

    override fun trackError(message: String) {
        _metrics.update { it.copy(lastError = message) }
    }

    override fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean) {
        val provider = initializers[clazz]
        if (provider != null) {
            loadModule(provider.get(), isParallel)
        }
    }

    override fun loadModule(initializer: ModuleInitializer, isParallel: Boolean) {
        val start = System.nanoTime()
        initializer.initialize()
        val durationMs = (System.nanoTime() - start) / 1_000_000.0
        
        if (isParallel) {
            _metrics.update { it.copy(parallelModuleLoadTimes = it.parallelModuleLoadTimes + (initializer.name to durationMs)) }
        } else {
            _metrics.update { it.copy(moduleLoadTimes = it.moduleLoadTimes + (initializer.name to durationMs)) }
        }
    }

    override fun trackAppStartup() {
        refreshStartupMetrics()
    }
}
