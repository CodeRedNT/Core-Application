package br.com.coderednt.coreapp.core.architecture

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import javax.inject.Inject

/**
 * Classe base para Activities que integra automaticamente o monitoramento de performance.
 *
 * Esta classe abstrai a configuração de Edge-to-Edge e fornece ganchos para
 * medir o tempo de inflação da UI e renderização.
 */
abstract class BaseActivity : ComponentActivity() {

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    /**
     * Nome identificador da Activity para fins de log e métricas.
     */
    protected open val activityName: String get() = this::class.java.simpleName

    /**
     * Define o conteúdo da tela utilizando Jetpack Compose.
     */
    @Composable
    abstract fun ScreenContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = System.nanoTime()
        super.onCreate(savedInstanceState)
        
        if (::performanceMonitor.isInitialized) {
            performanceMonitor.onStartActivityTracking(startTime)
        }
        
        enableEdgeToEdge()
        
        val inflationStart = System.nanoTime()
        setContent {
            ScreenContent()
        }
        
        if (::performanceMonitor.isInitialized) {
            performanceMonitor.onTrackUiInflation(inflationStart)
            performanceMonitor.onTrackRenderTime(activityName, window.decorView)
        }
    }

    override fun onResume() {
        super.onResume()
        if (::performanceMonitor.isInitialized) {
            performanceMonitor.onTrackMemory(activityName)
        }
        
        // Reporta que a UI está pronta para interação
        reportFullyDrawn()
    }
}
