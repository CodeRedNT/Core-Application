package br.com.coderednt.coreapp.core.common.base

import android.os.Bundle
import android.os.SystemClock
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.metrics.performance.JankStats
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import javax.inject.Inject

/**
 * BaseActivity profissional e desacoplada.
 */
abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    private var jankStats: JankStats? = null

    open val activityName: String get() = this::class.java.simpleName

    @Composable
    abstract fun ScreenContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = SystemClock.elapsedRealtime()
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        jankStats = JankStats.createAndTrack(window) { frameData ->
            if (frameData.isJank) {
                performanceMonitor.onJankDetected(
                    activityName, 
                    frameData.frameDurationUiNanos / 1_000_000
                )
            }
        }
        
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
