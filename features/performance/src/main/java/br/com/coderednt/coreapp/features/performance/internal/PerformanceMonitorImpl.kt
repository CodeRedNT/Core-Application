package br.com.coderednt.coreapp.features.performance.internal

import android.view.View
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceMonitorImpl @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) : PerformanceMonitor {

    private var onCreateStartNanos: Long = 0

    override fun onStartActivityTracking(startTimeNanos: Long) {
        onCreateStartNanos = startTimeNanos
        
        if (AppStartupTracker.appEndTimeNanos > 0) {
            val launchDelay = TimeUtils.nanosToMillis(TimeUtils.nowNanos() - AppStartupTracker.appEndTimeNanos)
            appHealthTracker.trackPhaseTime(StartupPhase.ACTIVITY_LAUNCH, launchDelay)
        }
    }

    override fun onTrackUiInflation(startNano: Long) {
        val durationMs = TimeUtils.nanosToMillis(TimeUtils.nowNanos() - startNano)
        appHealthTracker.trackPhaseTime(StartupPhase.UI_INFLATION, durationMs)
        
        if (!AppStartupTracker.isTtidReported) {
            AppStartupTracker.isTtidReported = true
            appHealthTracker.trackAppStartup()
        }
    }

    override fun onTrackRenderTime(activityName: String, decorView: View) {
        decorView.post {
            val renderTimeMs = TimeUtils.nanosToMillis(TimeUtils.nowNanos() - onCreateStartNanos)
            appHealthTracker.trackRenderTime(activityName, renderTimeMs.toLong())
        }
    }

    override fun onJankDetected(activityName: String, durationMs: Long) {
        // Agora reporta especificamente como Jank, sem poluir o log de erros/crashes
        appHealthTracker.trackJank(activityName)
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
