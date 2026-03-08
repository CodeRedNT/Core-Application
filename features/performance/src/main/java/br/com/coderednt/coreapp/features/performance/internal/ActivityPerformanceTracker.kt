package br.com.coderednt.coreapp.features.performance.internal

import android.os.SystemClock
import android.view.View
import br.com.coderednt.coreapp.core.common.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.common.performance.AppStartupTracker
import br.com.coderednt.coreapp.core.common.performance.StartupPhase
import javax.inject.Inject

class ActivityPerformanceTracker @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) {
    private var onCreateStartTime: Long = 0

    fun startTracking(startTime: Long) {
        onCreateStartTime = startTime
        if (AppStartupTracker.appEndTimeNanos > 0) {
            val launchDelay = (SystemClock.elapsedRealtimeNanos() - AppStartupTracker.appEndTimeNanos) / 1_000_000.0
            appHealthTracker.trackPhaseTime(StartupPhase.ACTIVITY_LAUNCH, launchDelay)
        }
    }

    fun trackUiInflation(startNano: Long) {
        val durationMs = (System.nanoTime() - startNano) / 1_000_000.0
        appHealthTracker.trackPhaseTime(StartupPhase.UI_INFLATION, durationMs)
        if (!AppStartupTracker.isTtidReported) {
            AppStartupTracker.isTtidReported = true
            appHealthTracker.trackAppStartup()
        }
    }

    fun trackRenderTime(activityName: String, decorView: View) {
        decorView.post {
            val renderTime = SystemClock.elapsedRealtime() - onCreateStartTime
            appHealthTracker.trackRenderTime(activityName, renderTime)
        }
    }
}
