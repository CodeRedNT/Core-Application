package br.com.coderednt.coreapp.features.performance.internal

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View
import androidx.metrics.performance.JankStats
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitoramento automático de Activities via Lifecycle Callbacks.
 * Remove a necessidade de todas as Activities herdarem de BaseActivity para métricas básicas.
 */
@Singleton
class PerformanceActivityLifecycleCallbacks @Inject constructor(
    private val performanceTracker: PerformanceMonitor
) : Application.ActivityLifecycleCallbacks {

    private val jankStatsMap = WeakHashMap<Activity, JankStats>()
    private val activityStartTimes = WeakHashMap<Activity, Long>()

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStartTimes[activity] = TimeUtils.nowNanos()
        
        // Configura JankStats para a janela da Activity
        val jankStats = JankStats.createAndTrack(activity.window) { frameData ->
            if (frameData.isJank) {
                performanceTracker.onJankDetected(
                    activity::class.java.simpleName,
                    frameData.frameDurationUiNanos / 1_000_000
                )
            }
        }
        jankStatsMap[activity] = jankStats
    }

    override fun onActivityStarted(activity: Activity) {
        val startTime = activityStartTimes[activity] ?: TimeUtils.nowNanos()
        performanceTracker.onStartActivityTracking(startTime)
    }

    override fun onActivityResumed(activity: Activity) {
        jankStatsMap[activity]?.isTrackingEnabled = true
        
        // Rastreia memória e tempo de renderização no primeiro quadro disponível
        val decorView = activity.window.decorView
        performanceTracker.onTrackRenderTime(activity::class.java.simpleName, decorView)
        performanceTracker.onTrackMemory(activity::class.java.simpleName)
    }

    override fun onActivityPaused(activity: Activity) {
        jankStatsMap[activity]?.isTrackingEnabled = false
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        jankStatsMap.remove(activity)
        activityStartTimes.remove(activity)
    }
}
