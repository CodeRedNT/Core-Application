package br.com.coderednt.coreapp.core.common.performance

import android.view.View

/**
 * Interface de contrato para monitoramento de performance de UI.
 * Permite que a BaseActivity envie sinais de métricas sem conhecer a implementação.
 */
interface PerformanceMonitor {
    /**
     * Sinaliza o início da criação da Activity.
     */
    fun onStartActivityTracking(startTimeMillis: Long)

    /**
     * Sinaliza o custo de inflação da UI (setContent).
     */
    fun onTrackUiInflation(startNano: Long)

    /**
     * Sinaliza o tempo total de renderização (TTFD).
     */
    fun onTrackRenderTime(activityName: String, decorView: View)
    
    /**
     * Sinaliza a detecção de um frame lento (Jank).
     */
    fun onJankDetected(activityName: String, durationMs: Long)
}
