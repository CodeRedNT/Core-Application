package br.com.coderednt.coreapp

import android.os.Bundle
import android.os.SystemClock
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.coderednt.coreapp.core.common.base.BaseActivity
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import br.com.coderednt.coreapp.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Ponto de entrada principal do aplicativo.
 * Gerencia a Splash Screen oficial e as fases iniciais de renderização.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashStart = SystemClock.elapsedRealtime()
        
        // Instala a Splash Screen oficial (Android 12+)
        val splashScreen = installSplashScreen()
        
        // Mantém a splash visível até que o conteúdo inicial seja carregado
        splashScreen.setKeepOnScreenCondition { false }
        
        super.onCreate(savedInstanceState)
        
        // Calcula a duração da Splash Screen
        val splashDuration = (SystemClock.elapsedRealtime() - splashStart).toDouble()
        
        // Reporta a métrica para a fase correta do startup
        performanceTracker.onTrackPhase(StartupPhase.SPLASH_SCREEN, splashDuration)
        
        // Inicia o rastreamento de tempo da Activity
        performanceTracker.onStartActivityTracking(SystemClock.elapsedRealtime())
    }

    @Composable
    override fun ScreenContent() {
        MainScreen()
    }
}
