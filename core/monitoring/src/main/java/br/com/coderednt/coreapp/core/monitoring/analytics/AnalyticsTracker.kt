package br.com.coderednt.coreapp.core.monitoring.analytics

interface AnalyticsTracker {
    fun logEvent(name: String, params: Map<String, Any> = emptyMap())
    
    companion object {
        const val EVENT_APP_STARTUP_TIME = "app_startup_time"
        const val EVENT_MODULE_LOAD_TIME = "module_load_time"
        const val EVENT_FRAME_RENDER_TIME = "frame_render_time"
        
        const val PARAM_TIME_MS = "time_ms"
        const val PARAM_MODULE_NAME = "module_name"
        const val PARAM_SCREEN_NAME = "screen_name"
    }
}
