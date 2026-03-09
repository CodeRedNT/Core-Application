package br.com.coderednt.coreapp.features.performance.internal

import android.view.View
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do [PerformanceMonitor] para o SDK de Performance.
 * 
 * Esta classe atua como uma ponte entre os eventos de ciclo de vida da Activity 
 * (capturados via BaseActivity) e o [AppHealthTracker], processando os tempos 
 * e convertendo-os para milissegundos antes de reportar.
 * 
 * @property appHealthTracker O rastreador central de saúde que consolida as métricas.
 */
@Singleton
class PerformanceMonitorImpl @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) : PerformanceMonitor {

    private var onCreateStartNanos: Long = 0

    /**
     * Inicia o rastreamento do tempo de vida de uma Activity.
     * Calcula o atraso entre o final da Application e o início da Activity (Activity Launch Delay).
     */
    override fun onStartActivityTracking(startTimeNanos: Long) {
        onCreateStartNanos = startTimeNanos
        
        if (AppStartupTracker.appEndTimeNanos > 0) {
            val launchDelay = TimeUtils.nanosToMillis(TimeUtils.nowNanos() - AppStartupTracker.appEndTimeNanos)
            appHealthTracker.trackPhaseTime(StartupPhase.ACTIVITY_LAUNCH, launchDelay)
        }
    }

    /**
     * Registra o tempo gasto na inflação da UI.
     * Se for a primeira tela, marca o Time To Initial Display (TTID) no tracker de startup.
     */
    override fun onTrackUiInflation(startNano: Long) {
        val durationMs = TimeUtils.nanosToMillis(TimeUtils.nowNanos() - startNano)
        appHealthTracker.trackPhaseTime(StartupPhase.UI_INFLATION, durationMs)
        
        if (!AppStartupTracker.isTtidReported) {
            AppStartupTracker.isTtidReported = true
            appHealthTracker.trackAppStartup()
        }
    }

    /**
     * Mede o tempo de renderização postando um runnable na fila de mensagens da UI.
     * Isso garante que a medição ocorra após o primeiro frame ser desenhado.
     */
    override fun onTrackRenderTime(activityName: String, decorView: View) {
        decorView.post {
            val renderTimeMs = TimeUtils.nanosToMillis(TimeUtils.nowNanos() - onCreateStartNanos)
            appHealthTracker.trackRenderTime(activityName, renderTimeMs.toLong())
        }
    }

    /**
     * Reporta a detecção de Jank (engasgos na UI).
     */
    override fun onJankDetected(activityName: String, durationMs: Long) {
        appHealthTracker.trackJank(activityName)
    }

    /**
     * Registra a duração de uma fase genérica do ciclo de vida.
     */
    override fun onTrackPhase(phase: StartupPhase, durationMs: Double) {
        appHealthTracker.trackPhaseTime(phase, durationMs)
    }

    /**
     * Realiza uma medição instantânea do uso de memória (Heap) da JVM.
     */
    override fun onTrackMemory(activityName: String) {
        val runtime = Runtime.getRuntime()
        val usedMb = (runtime.totalMemory() - runtime.freeMemory()) / (1024.0 * 1024.0)
        appHealthTracker.trackActivityMemory(activityName, usedMb)
    }
}
