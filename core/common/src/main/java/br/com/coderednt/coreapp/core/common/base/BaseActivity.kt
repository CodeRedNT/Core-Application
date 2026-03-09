package br.com.coderednt.coreapp.core.common.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import javax.inject.Inject

/**
 * BaseActivity com monitoramento automático de inflação de UI.
 */
abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    open val activityName: String get() = this::class.java.simpleName

    @Composable
    abstract fun ScreenContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = TimeUtils.nowNanos()
        
        // super.onCreate() deve ser chamado antes de acessar propriedades injetadas pelo Hilt
        super.onCreate(savedInstanceState)
        
        performanceMonitor.onStartActivityTracking(startTime)

        enableEdgeToEdge()
        
        val inflationStart = TimeUtils.nowNanos()
        setContent {
            ScreenContent()
        }
        performanceMonitor.onTrackUiInflation(inflationStart)
        
        performanceMonitor.onTrackRenderTime(activityName, window.decorView)
    }

    override fun onResume() {
        super.onResume()
        performanceMonitor.onTrackMemory(activityName)
        reportFullyDrawn()
    }
}
