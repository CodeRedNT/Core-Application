package br.com.coderednt.coreapp.features.performance.internal

import android.os.SystemClock
import android.view.View
import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceMonitorImpl @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) : PerformanceMonitor {

    private var onCreateStartTime: Long = 0

    override fun onStartActivityTracking(startTimeMillis: Long) {
        onCreateStartTime = startTimeMillis
        
        if (AppStartupTracker.appEndTimeNanos > 0) {
            val launchDelay = (SystemClock.elapsedRealtimeNanos() - AppStartupTracker.appEndTimeNanos) / 1_000_000.0
            appHealthTracker.trackPhaseTime(StartupPhase.ACTIVITY_LAUNCH, launchDelay)
        }
    }

    override fun onTrackUiInflation(startNano: Long) {
        val durationMs = (System.nanoTime() - startNano) / 1_000_000.0
        appHealthTracker.trackPhaseTime(StartupPhase.UI_INFLATION, durationMs)
        
        if (!AppStartupTracker.isTtidReported) {
            AppStartupTracker.isTtidReported = true
            appHealthTracker.trackAppStartup()
        }
    }

    override fun onTrackRenderTime(activityName: String, decorView: View) {
        decorView.post {
            val renderTime = SystemClock.elapsedRealtime() - onCreateStartTime
            appHealthTracker.trackRenderTime(activityName, renderTime)
        }
    }

    override fun onJankDetected(activityName: String, durationMs: Long) {
        appHealthTracker.trackError("Jank detected in $activityName: ${durationMs}ms")
    }

    override fun onTrackPhase(phase: StartupPhase, durationMs: Double) {
        appHealthTracker.trackPhaseTime(phase, durationMs)
    }

    override fun onTrackMemory(activityName: String) {
        val runtime = Runtime.getRuntime()
        val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
        appHealthTracker.trackActivityMemory(activityName, usedMb)
    }
}
