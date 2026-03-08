package br.com.coderednt.coreapp.core.common.base

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable

/**
 * BaseActivity simplificada.
 * O monitoramento de performance agora é feito automaticamente via PerformanceActivityLifecycleCallbacks,
 * removendo o acoplamento direto com JankStats e PerformanceMonitor aqui.
 */
abstract class BaseActivity : ComponentActivity() {

    open val activityName: String get() = this::class.java.simpleName

    @Composable
    abstract fun ScreenContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ScreenContent()
        }
    }

    override fun onResume() {
        super.onResume()
        reportFullyDrawn()
    }
}
