package br.com.coderednt.coreapp

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.coderednt.coreapp.core.common.base.BaseActivity
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.PerformanceMonitor
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import br.com.coderednt.coreapp.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Ponto de entrada principal do aplicativo.
 * Gerencia a Splash Screen oficial e as fases iniciais de renderização.
 * Atualizada para usar o novo sistema de monitoramento automático e TimeUtils.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var performanceMonitor: PerformanceMonitor

    override fun onCreate(savedInstanceState: Bundle?) {
        // Marca o início da Splash Screen usando o utilitário padronizado
        val splashStartNanos = TimeUtils.nowNanos()
        
        // Instala a Splash Screen oficial (Android 12+)
        val splashScreen = installSplashScreen()
        
        // Define a condição para manter a splash na tela (pode ser expandido com estados de UI)
        splashScreen.setKeepOnScreenCondition { false }
        
        super.onCreate(savedInstanceState)
        
        // Calcula a duração da Splash Screen e reporta via PerformanceMonitor
        val splashDurationMs = TimeUtils.calculateDurationFrom(splashStartNanos)
        performanceMonitor.onTrackPhase(StartupPhase.SPLASH_SCREEN, splashDurationMs)
        
        // O rastreamento de Activity (onStartActivityTracking) agora é feito 
        // automaticamente pelo PerformanceActivityLifecycleCallbacks.
    }

    @Composable
    override fun ScreenContent() {
        MainScreen()
    }
}
