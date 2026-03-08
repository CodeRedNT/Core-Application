package br.com.coderednt.coreapp.core.common.base

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.metrics.performance.JankStats
import br.com.coderednt.coreapp.core.common.performance.ActivityPerformanceTracker
import br.com.coderednt.coreapp.core.common.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.common.performance.AppStartupTracker
import javax.inject.Inject

abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var performanceTracker: ActivityPerformanceTracker

    @Inject
    lateinit var appHealthTracker: AppHealthTracker

    private lateinit var jankStats: JankStats

    /**
     * Nome identificador para as métricas de renderização. 
     * Abstrai automaticamente do nome da própria Activity.
     */
    open val activityName: String get() = this::class.java.simpleName

    /**
     * Conteúdo Compose da tela. Deve ser implementado pelas subclasses.
     */
    @Composable
    abstract fun ScreenContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = SystemClock.elapsedRealtime()
        super.onCreate(savedInstanceState)
        
        // Configuração automática de sistema
        enableEdgeToEdge()
        
        // Inicializa JankStats para monitorar frames lentos
        jankStats = JankStats.createAndTrack(window) { frameData ->
            if (frameData.isJank) {
                appHealthTracker.trackError("Jank detected in $activityName: ${frameData.frameDurationUiNanos / 1_000_000}ms")
            }
        }
        
        // Início do rastreamento de performance
        performanceTracker.startTracking(startTime)
        
        // Define o conteúdo e rastreia inflação
        val startNano = System.nanoTime()
        setContent {
            ScreenContent()
        }
        performanceTracker.trackUiInflation(startNano)
    }

    override fun onResume() {
        super.onResume()
        jankStats.isTrackingEnabled = true
        // Report fully drawn para métricas de TTFD
        reportFullyDrawn()
        // Rastreia o tempo final de renderização do frame
        performanceTracker.trackRenderTime(activityName, window.decorView)
    }

    override fun onPause() {
        super.onPause()
        jankStats.isTrackingEnabled = false
    }
}
