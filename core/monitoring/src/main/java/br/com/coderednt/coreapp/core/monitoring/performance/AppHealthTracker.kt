package br.com.coderednt.coreapp.core.monitoring.performance

import kotlinx.coroutines.flow.StateFlow

data class HealthMetrics(
    val osOverheadTimeMs: Double = 0.0,
    val providerInitTimeMs: Double = 0.0,
    val diInitializationTimeMs: Double = 0.0,
    val appOnCreateTimeMs: Double = 0.0,
    val activityLaunchDelayMs: Double = 0.0,
    val uiInflationTimeMs: Double = 0.0,
    val splashScreenDurationMs: Double = 0.0,
    val moduleLoadTimes: Map<String, Double> = emptyMap(),
    val parallelModuleLoadTimes: Map<String, Double> = emptyMap(),
    val renderTimes: Map<String, Long> = emptyMap(),
    val navigationTimes: Map<String, Long> = emptyMap(),
    val lastError: String? = null
) {
    val startupTimeMs: Double 
        get() = osOverheadTimeMs + providerInitTimeMs + diInitializationTimeMs + 
                appOnCreateTimeMs + moduleLoadTimes.values.sum() + 
                activityLaunchDelayMs + uiInflationTimeMs + splashScreenDurationMs
}

interface AppHealthTracker {
    val metrics: StateFlow<HealthMetrics>
    
    fun onAppStart()
    fun onAppEnd()

    fun trackRenderTime(screenName: String, timeMillis: Long)
    fun trackNavigationTime(route: String, durationMs: Long)
    fun trackPhaseTime(phase: StartupPhase, durationMs: Double)
    fun trackError(message: String)
    fun loadModule(initializer: ModuleInitializer, isParallel: Boolean = false)
    fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean = false)
    fun trackAppStartup()
}

enum class StartupPhase {
    OS_OVERHEAD,
    PROVIDER_INIT,
    DI_INIT,
    APP_ONCREATE,
    ACTIVITY_LAUNCH,
    UI_INFLATION,
    SPLASH_SCREEN
}

// --- DSL de Inicialização ---

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
