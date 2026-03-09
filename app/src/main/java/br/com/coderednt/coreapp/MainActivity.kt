package br.com.coderednt.coreapp

import android.os.Bundle
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.coderednt.coreapp.core.common.base.BaseActivity
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import br.com.coderednt.coreapp.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Ponto de entrada principal do aplicativo.
 * Gerencia a Splash Screen oficial e as fases iniciais de renderização.
 * Atualizada para exibir os módulos registrados e suportar monitoramento automático.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    @Inject
    lateinit var registeredModules: Map<Class<out ModuleInitializer>, @JvmSuppressWildcards ModuleInitializer>

    override fun onCreate(savedInstanceState: Bundle?) {
        // Marca o início da Splash Screen usando o utilitário padronizado
        val splashStartNanos = TimeUtils.nowNanos()
        
        // Instala a Splash Screen oficial (Android 12+)
        val splashScreen = installSplashScreen()
        
        // Define a condição para manter a splash na tela (pode ser expandido com estados de UI)
        splashScreen.setKeepOnScreenCondition { false }
        
        super.onCreate(savedInstanceState)
        
        // Log de visibilidade dos módulos registrados no grafo do Hilt
        logRegisteredModules()
        
        // Calcula a duração da Splash Screen e reporta via PerformanceMonitor
        val splashDurationMs = TimeUtils.calculateDurationFrom(splashStartNanos)
        performanceMonitor.onTrackPhase(StartupPhase.SPLASH_SCREEN, splashDurationMs)
    }

    private fun logRegisteredModules() {
        val moduleNames = registeredModules.values.joinToString(", ") { it.name }
        Log.i("MainActivity", "Módulos registrados para monitoramento: [$moduleNames]")
    }

    @Composable
    override fun ScreenContent() {
        MainScreen()
    }
}
