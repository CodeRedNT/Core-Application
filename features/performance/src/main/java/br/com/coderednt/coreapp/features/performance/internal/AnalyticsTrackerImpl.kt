package br.com.coderednt.coreapp.features.performance.internal

import android.util.Log
import br.com.coderednt.coreapp.core.monitoring.analytics.AnalyticsTracker
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsTrackerImpl @Inject constructor() : AnalyticsTracker {
    override fun logEvent(name: String, params: Map<String, Any>) {
        Log.i("PerformanceMetrics", "[$name] ${params.entries.joinToString { "${it.key}: ${it.value}" }}")
    }
}
