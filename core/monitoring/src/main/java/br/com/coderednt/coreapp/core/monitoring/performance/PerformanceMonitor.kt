package br.com.coderednt.coreapp.core.monitoring.performance

import android.view.View

/**
 * Interface para monitoramento de performance de UI e eventos de ciclo de vida.
 */
interface PerformanceMonitor {
    /**
     * Inicia o rastreamento de tempo de uma Activity.
     */
    fun onStartActivityTracking(startTimeMillis: Long)

    /**
     * Registra o tempo gasto na inflação da hierarquia de Views (Compose ou XML).
     */
    fun onTrackUiInflation(startNano: Long)

    /**
     * Registra o tempo de renderização (primeiro quadro) de uma tela.
     */
    fun onTrackRenderTime(activityName: String, decorView: View)

    /**
     * Reporta a detecção de quadros lentos (Jank) ou durações de fases específicas.
     */
    fun onJankDetected(activityName: String, durationMs: Long)

    /**
     * Registra a duração de uma fase específica do startup, como a Splash Screen.
     */
    fun onTrackPhase(phase: StartupPhase, durationMs: Double)

    /**
     * Registra o uso de memória de uma Activity específica.
     */
    fun onTrackMemory(activityName: String)
}
