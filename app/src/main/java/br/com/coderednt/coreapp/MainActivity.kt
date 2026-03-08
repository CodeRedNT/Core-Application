package br.com.coderednt.coreapp

import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.compose.runtime.Composable
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import br.com.coderednt.coreapp.core.common.base.BaseActivity
import br.com.coderednt.coreapp.core.common.performance.AppStartupTracker
import br.com.coderednt.coreapp.core.common.performance.StartupPhase
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Instala a Splash Screen da API oficial
        val splashScreen = installSplashScreen()
        
        super.onCreate(savedInstanceState)

        // Adiciona um efeito de saída (Animation) e trackeia o tempo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                val splashStart = System.nanoTime()
                
                // Efeito de subida com transparência
                val slideUp = ObjectAnimator.ofFloat(
                    splashScreenView.view,
                    View.TRANSLATION_Y,
                    0f,
                    -splashScreenView.view.height.toFloat()
                )
                slideUp.interpolator = AnticipateInterpolator()
                slideUp.duration = 500L

                slideUp.doOnEnd {
                    val durationMs = (System.nanoTime() - splashStart) / 1_000_000.0
                    appHealthTracker.trackPhaseTime(StartupPhase.SPLASH_SCREEN, durationMs)
                    splashScreenView.remove()
                }

                slideUp.start()
            }
        }
    }

    @Composable
    override fun ScreenContent() {
        CoreApp(appHealthTracker)
    }
}
