package br.com.coderednt.coreapp.core.monitoring.performance

import android.view.View

interface PerformanceMonitor {
    fun onStartActivityTracking(startTimeMillis: Long)
    fun onTrackUiInflation(startNano: Long)
    fun onTrackRenderTime(activityName: String, decorView: View)
    fun onJankDetected(activityName: String, durationMs: Long)
}
