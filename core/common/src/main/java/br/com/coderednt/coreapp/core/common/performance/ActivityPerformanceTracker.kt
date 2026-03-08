package br.com.coderednt.coreapp.core.common.performance

import android.os.SystemClock
import android.view.View
import javax.inject.Inject

class ActivityPerformanceTracker @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) {
    private var onCreateStartTime: Long = 0

    /**
     * Marca o início da criação da Activity.
     * @param startTime Tempo capturado manualmente para precisão máxima antes do super.onCreate
     */
    fun startTracking(startTime: Long) {
        onCreateStartTime = startTime
        
        // Calcula o delay entre o fim do App onCreate e o início da Activity
        if (AppStartupTracker.appEndTimeNanos > 0) {
            val launchDelay = (SystemClock.elapsedRealtimeNanos() - AppStartupTracker.appEndTimeNanos) / 1_000_000.0
            appHealthTracker.trackPhaseTime(StartupPhase.ACTIVITY_LAUNCH, launchDelay)
        }
    }

    /**
     * Rastreia o custo de inflação da UI (setContent).
     */
    fun trackUiInflation(startNano: Long) {
        val durationMs = (System.nanoTime() - startNano) / 1_000_000.0
        appHealthTracker.trackPhaseTime(StartupPhase.UI_INFLATION, durationMs)
        
        // O TTID real termina AQUI (após a inflação do primeiro frame da primeira Activity)
        if (!AppStartupTracker.isTtidReported) {
            AppStartupTracker.isTtidReported = true
            appHealthTracker.trackAppStartup()
        }
    }

    /**
     * Rastreia o tempo de renderização (TTFD) usando o decorView.
     */
    fun trackRenderTime(activityName: String, decorView: View) {
        decorView.post {
            val renderTime = SystemClock.elapsedRealtime() - onCreateStartTime
            appHealthTracker.trackRenderTime(activityName, renderTime)
        }
    }
}
