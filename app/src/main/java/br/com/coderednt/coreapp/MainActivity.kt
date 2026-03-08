package br.com.coderednt.coreapp

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.coderednt.coreapp.core.common.base.BaseActivity
import br.com.coderednt.coreapp.ui.MainScreen
import dagger.hilt.android.AndroidEntryPoint

/**
 * Ponto de entrada principal do aplicativo.
 */
@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instala a Splash Screen oficial
        installSplashScreen()
        
        super.onCreate(savedInstanceState)
        
        // A lógica de animação customizada da Splash pode ser movida para um Initializer 
        // ou Utilitário no futuro se for complexa demais.
    }

    @Composable
    override fun ScreenContent() {
        MainScreen()
    }
}
