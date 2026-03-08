package br.com.coderednt.coreapp.core.common.base

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.metrics.performance.JankStats
import br.com.coderednt.coreapp.core.common.performance.PerformanceMonitor
import javax.inject.Inject

/**
 * BaseActivity profissional e desacoplada.
 * Proverá suporte básico para todas as telas do ecossistema, permitindo
 * monitoramento de performance de forma opcional via [PerformanceMonitor].
 */
abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    private var jankStats: JankStats? = null

    /**
     * Nome identificador para as métricas de renderização. 
     */
    open val activityName: String get() = this::class.java.simpleName

    /**
     * Conteúdo Compose da tela.
     */
    @Composable
    abstract fun ScreenContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = SystemClock.elapsedRealtime()
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        // Inicializa JankStats
        jankStats = JankStats.createAndTrack(window) { frameData ->
            if (frameData.isJank) {
                performanceMonitor.onJankDetected(
                    activityName, 
                    frameData.frameDurationUiNanos / 1_000_000
                )
            }
        }
        
        // Sinaliza início via interface
        performanceMonitor.onStartActivityTracking(startTime)
        
        val startNano = System.nanoTime()
        setContent {
            ScreenContent()
        }
        performanceMonitor.onTrackUiInflation(startNano)
    }

    override fun onResume() {
        super.onResume()
        jankStats?.isTrackingEnabled = true
        reportFullyDrawn()
        performanceMonitor.onTrackRenderTime(activityName, window.decorView)
    }

    override fun onPause() {
        super.onPause()
        jankStats?.isTrackingEnabled = false
    }
}
