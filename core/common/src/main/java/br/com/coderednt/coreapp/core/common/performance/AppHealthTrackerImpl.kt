package br.com.coderednt.coreapp.core.common.performance

import android.os.SystemClock
import br.com.coderednt.coreapp.core.common.analytics.AnalyticsTracker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
    private val scope = CoroutineScope(Dispatchers.IO)
    
    private var manifestSyncModulesDuration = 0.0

    override fun trackRenderTime(screenName: String, timeMillis: Long) {
        _metrics.update { 
            it.copy(renderTimes = it.renderTimes + (screenName to timeMillis))
        }
        analyticsTracker.logEvent(
            AnalyticsTracker.EVENT_FRAME_RENDER_TIME,
            mapOf(
                AnalyticsTracker.PARAM_SCREEN_NAME to screenName,
                AnalyticsTracker.PARAM_TIME_MS to timeMillis
            )
        )
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

    override fun trackError(message: String) {
        _metrics.update { it.copy(lastError = message) }
        analyticsTracker.logEvent("app_critical_error", mapOf("message" to message))
    }

    override fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean) {
        val provider = initializers[clazz] ?: return
        if (isParallel) {
            scope.launch { executeLoad(provider, true) }
        } else {
            executeLoad(provider, false)
        }
    }

    override fun startManifest(block: AppHealthTracker.() -> Unit) {
        // OS Overhead e Provider Init baseados no AppStartupTracker
        val osOverhead = (AppStartupTracker.providerStartTimeNanos - AppStartupTracker.processStartTimeNanos) / 1_000_000.0
        val providerInit = (AppStartupTracker.appStartTimeNanos - AppStartupTracker.providerStartTimeNanos) / 1_000_000.0
        
        trackPhaseTime(StartupPhase.OS_OVERHEAD, osOverhead)
        trackPhaseTime(StartupPhase.PROVIDER_INIT, providerInit)

        manifestSyncModulesDuration = 0.0
        val manifestStart = System.nanoTime()
        this.block()
        val manifestDuration = (System.nanoTime() - manifestStart) / 1_000_000.0
        
        val appDslOverhead = manifestDuration - manifestSyncModulesDuration
        trackPhaseTime(StartupPhase.APP_ONCREATE, if (appDslOverhead > 0) appDslOverhead else 0.01)
    }

    override fun trackAppStartup() {
        // O TTID agora é puramente derivado do estado das métricas, garantindo sincronia total.
    }

    override fun loadModule(initializer: ModuleInitializer) {
        val start = System.nanoTime()
        initializer.initialize()
        val durationMs = (System.nanoTime() - start) / 1_000_000.0
        recordDuration(initializer.name, durationMs, initializer.isParallel)
    }

    override fun loadModules(
        sync: List<Class<out ModuleInitializer>>,
        async: List<Class<out ModuleInitializer>>
    ) {
        sync.forEach { load(it, false) }
        async.forEach { load(it, true) }
    }

    private fun executeLoad(provider: Provider<ModuleInitializer>, isParallel: Boolean) {
        val start = System.nanoTime()
        val instance = provider.get()
        instance.initialize()
        val durationMs = (System.nanoTime() - start) / 1_000_000.0
        
        if (!isParallel) {
            manifestSyncModulesDuration += durationMs
        }
        
        recordDuration(instance.name, durationMs, isParallel)
    }

    private fun recordDuration(name: String, durationMs: Double, isParallel: Boolean) {
        if (isParallel) {
            _metrics.update { 
                it.copy(parallelModuleLoadTimes = it.parallelModuleLoadTimes + (name to durationMs))
            }
        } else {
            _metrics.update { 
                it.copy(moduleLoadTimes = it.moduleLoadTimes + (name to durationMs))
            }
        }
    }
}
