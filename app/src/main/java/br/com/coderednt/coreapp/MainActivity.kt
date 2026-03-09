package br.com.coderednt.coreapp

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.coderednt.coreapp.core.architecture.BaseActivity
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import br.com.coderednt.coreapp.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Activity principal do aplicativo Core-Application.
 * 
 * Estende [BaseActivity] para herdar o monitoramento automático de inflação de UI, 
 * renderização e consumo de recursos. Gerencia a Splash Screen do sistema e 
 * orquestra a exibição da [MainScreen].
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Captura o início da inicialização da Splash Screen
        val splashStartNanos = TimeUtils.nowNanos()
        
        // Instala a API de Splash Screen nativa (Android 12+)
        val splashScreen = installSplashScreen()
        
        // A Splash Screen é removida assim que o primeiro quadro da MainScreen é desenhado
        splashScreen.setKeepOnScreenCondition { false }
        
        super.onCreate(savedInstanceState)
        
        // Registra a duração da Splash Screen para análise no Dashboard de Performance
        val splashDurationMs = TimeUtils.calculateDurationFrom(splashStartNanos)
        performanceMonitor.onTrackPhase(StartupPhase.SPLASH_SCREEN, splashDurationMs)
    }

    /**
     * Define o ponto de entrada da interface Compose.
     */
    @Composable
    override fun ScreenContent() {
        MainScreen()
    }
}
