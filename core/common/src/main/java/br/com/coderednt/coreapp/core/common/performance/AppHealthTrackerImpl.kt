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
        _metrics.update { 
            it.copy(renderTimes = it.renderTimes + (screenName to timeMillis))
        }
    }

    override fun trackNavigationTime(route: String, durationMs: Long) {
        _metrics.update { 
            it.copy(navigationTimes = it.navigationTimes + (route to durationMs))
        }
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
    }

    override fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean) {
        // 1. Tenta buscar a instância no mapa do Hilt (para inicializadores que precisam de @Inject)
        val provider = initializers[clazz]
        
        if (provider != null) {
            loadModule(provider.get(), isParallel)
        } else {
            // 2. Fallback: Se não houver DI, tenta instanciar via reflexão (requer construtor vazio)
            try {
                val instance = clazz.getDeclaredConstructor().newInstance()
                loadModule(instance, isParallel)
            } catch (e: Exception) {
                trackError("Falha ao instanciar modulo ${clazz.simpleName}: ${e.message}")
            }
        }
    }

    override fun loadModule(initializer: ModuleInitializer, isParallel: Boolean) {
        val start = System.nanoTime()
        initializer.initialize()
        val durationMs = (System.nanoTime() - start) / 1_000_000.0
        
        if (isParallel) {
            _metrics.update { 
                it.copy(parallelModuleLoadTimes = it.parallelModuleLoadTimes + (initializer.name to durationMs))
            }
        } else {
            _metrics.update { 
                it.copy(moduleLoadTimes = it.moduleLoadTimes + (initializer.name to durationMs))
            }
        }
    }

    override fun trackAppStartup() {
        refreshStartupMetrics()
    }
}
