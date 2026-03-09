package br.com.coderednt.coreapp.core.monitoring.performance

import android.view.View

/**
 * Interface de alto nível para monitoramento de performance de UI e eventos de ciclo de vida.
 * 
 * Esta interface define os contratos para capturar métricas vitais da interface do usuário,
 * como tempo de inflação, renderização de frames e detecção de lentidão (Jank).
 */
interface PerformanceMonitor {
    
    /**
     * Inicia o rastreamento do tempo de ciclo de vida de uma Activity.
     * 
     * @param startTimeNanos O carimbo de data/hora de início em nanossegundos, 
     * preferencialmente obtido via SystemClock.elapsedRealtimeNanos().
     */
    fun onStartActivityTracking(startTimeNanos: Long)

    /**
     * Registra o tempo gasto na inflação da hierarquia de Views ou composição do Jetpack Compose.
     * 
     * @param startNano O momento em que a inflação/composição foi iniciada.
     */
    fun onTrackUiInflation(startNano: Long)

    /**
     * Registra o tempo decorrido até que o primeiro quadro (frame) da tela seja efetivamente renderizado.
     * 
     * @param activityName O nome identificador da tela (geralmente o simpleName da classe).
     * @param decorView A View raiz da janela para monitoramento via ViewTreeObserver.
     */
    fun onTrackRenderTime(activityName: String, decorView: View)

    /**
     * Reporta a detecção de quadros lentos (Jank) que podem impactar a experiência do usuário.
     * 
     * @param activityName A tela onde a lentidão foi detectada.
     * @param durationMs A duração do frame lento em milissegundos.
     */
    fun onJankDetected(activityName: String, durationMs: Long)

    /**
     * Registra a duração de uma fase específica do processo de inicialização (Startup).
     * 
     * @param phase A fase sendo medida (ex: Splash Screen, DI).
     * @param durationMs A duração da fase em milissegundos.
     */
    fun onTrackPhase(phase: StartupPhase, durationMs: Double)

    /**
     * Captura o uso atual de memória (Heap) associado a uma tela específica.
     * 
     * @param activityName O nome da tela ativa no momento da medição.
     */
    fun onTrackMemory(activityName: String)
}
