package br.com.coderednt.coreapp.features.performance.internal

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.metrics.performance.JankStats
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Monitoramento automático de Activities via Lifecycle Callbacks do Android.
 * 
 * Esta classe elimina a necessidade de que todas as Activities herdem manualmente 
 * de uma classe base para métricas de performance básicas. Ela injeta automaticamente 
 * o monitoramento de Jank (quadros lentos) e tempo de renderização em cada Activity 
 * que é criada no sistema.
 * 
 * @property performanceTracker O monitor de performance que receberá os dados processados.
 */
@Singleton
class PerformanceActivityLifecycleCallbacks @Inject constructor(
    private val performanceTracker: PerformanceMonitor
) : Application.ActivityLifecycleCallbacks {

    /** Mapa de instâncias JankStats por Activity para gerenciar o ciclo de vida do rastreamento. */
    private val jankStatsMap = WeakHashMap<Activity, JankStats>()
    
    /** Armazena o timestamp de criação de cada Activity para cálculo de tempo de carga. */
    private val activityStartTimes = WeakHashMap<Activity, Long>()

    /**
     * Chamado quando uma Activity é criada. 
     * Configura o JankStats para monitorar frames lentos na janela da Activity.
     */
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        activityStartTimes[activity] = TimeUtils.nowNanos()
        
        // Inicializa o JankStats da biblioteca Jetpack Metrics
        val jankStats = JankStats.createAndTrack(activity.window) { frameData ->
            if (frameData.isJank) {
                // Notifica o tracker sobre a detecção de um quadro lento
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
        // Ativa o rastreamento apenas quando a Activity está em primeiro plano
        jankStatsMap[activity]?.isTrackingEnabled = true
        
        val decorView = activity.window.decorView
        performanceTracker.onTrackRenderTime(activity::class.java.simpleName, decorView)
        performanceTracker.onTrackMemory(activity::class.java.simpleName)
    }

    override fun onActivityPaused(activity: Activity) {
        // Desativa o rastreamento em background para economizar recursos
        jankStatsMap[activity]?.isTrackingEnabled = false
    }

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    /**
     * Limpa as referências para evitar vazamentos de memória (Memory Leaks).
     */
    override fun onActivityDestroyed(activity: Activity) {
        jankStatsMap.remove(activity)
        activityStartTimes.remove(activity)
    }
}
