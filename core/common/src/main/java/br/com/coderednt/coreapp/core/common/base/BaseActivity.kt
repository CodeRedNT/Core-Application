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
 * BaseActivity fornece a infraestrutura base para todas as Activities do projeto.
 * 
 * Implementa automaticamente:
 * 1. Suporte a Edge-to-Edge (System Bars transparentes).
 * 2. Monitoramento de performance e detecção de frames lentos (Jank).
 * 3. Integração nativa com Jetpack Compose através de [ScreenContent].
 * 4. Rastreamento de uso de memória por Activity.
 */
abstract class BaseActivity : ComponentActivity() {

    /**
     * Rastreador de performance injetado via Hilt.
     * Utilizado para medir tempos de renderização e inflação da UI.
     */
    @Inject
    lateinit var performanceTracker: PerformanceMonitor

    /**
     * Instância do JankStats para monitoramento de quadros da janela atual.
     */
    private var jankStats: JankStats? = null

    /**
     * Nome identificador da Activity para logs e métricas.
     * Pode ser sobrescrito se necessário.
     */
    open val activityName: String get() = this::class.java.simpleName

    /**
     * Define o conteúdo da tela em Compose.
     * Deve ser implementado pelas Activities filhas.
     */
    @Composable
    abstract fun ScreenContent()

    /**
     * Inicializa a Activity, configura o Edge-to-Edge e inicia o rastreamento de performance.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        val startTime = SystemClock.elapsedRealtime()
        super.onCreate(savedInstanceState)
        
        enableEdgeToEdge()
        
        jankStats = JankStats.createAndTrack(window) { frameData ->
            if (frameData.isJank) {
                performanceTracker.onJankDetected(
                    activityName, 
                    frameData.frameDurationUiNanos / 1_000_000
                )
            }
        }
        
        performanceTracker.onStartActivityTracking(startTime)
        
        val startNano = System.nanoTime()
        setContent {
            ScreenContent()
        }
        
        performanceTracker.onTrackUiInflation(startNano)
    }

    /**
     * Retoma o rastreamento de jank e reporta que a Activity está totalmente desenhada.
     * Também realiza o rastreamento de uso de memória da Activity.
     */
    override fun onResume() {
        super.onResume()
        jankStats?.isTrackingEnabled = true
        reportFullyDrawn()
        performanceTracker.onTrackRenderTime(activityName, window.decorView)
        
        // Rastreia o uso de memória ao retomar a Activity
        performanceTracker.onTrackMemory(activityName)
    }

    /**
     * Pausa o rastreamento de jank para economizar recursos quando a Activity não está visível.
     */
    override fun onPause() {
        super.onPause()
        jankStats?.isTrackingEnabled = false
    }
}
