package br.com.coderednt.coreapp.core.common.performance

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
    val lastError: String? = null
) {
    /**
     * TTID Centralizado: A soma exata de todas as fases síncronas que compõem o startup.
     * Isso garante que não existam divergências no Dashboard.
     */
    val startupTimeMs: Double 
        get() = osOverheadTimeMs + providerInitTimeMs + diInitializationTimeMs + 
                appOnCreateTimeMs + moduleLoadTimes.values.sum() + 
                activityLaunchDelayMs + uiInflationTimeMs + splashScreenDurationMs
}

interface AppHealthTracker {
    val metrics: StateFlow<HealthMetrics>
    fun trackRenderTime(screenName: String, timeMillis: Long)
    fun trackPhaseTime(phase: StartupPhase, durationMs: Double)
    fun trackError(message: String)
    fun <T : ModuleInitializer> load(clazz: Class<T>, isParallel: Boolean = false)
    fun loadModule(initializer: ModuleInitializer)
    fun loadModules(
        sync: List<Class<out ModuleInitializer>>,
        async: List<Class<out ModuleInitializer>>
    )
    fun startManifest(block: AppHealthTracker.() -> Unit)
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
