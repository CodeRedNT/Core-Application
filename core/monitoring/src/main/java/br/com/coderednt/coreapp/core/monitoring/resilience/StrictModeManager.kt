package br.com.coderednt.coreapp.core.monitoring.resilience

import android.os.Build
import android.os.StrictMode
import br.com.coderednt.coreapp.core.logging.Logger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Gerenciador de políticas de resiliência em tempo de execução.
 * 
 * Utiliza o StrictMode do Android para detectar e reportar violações de thread principal 
 * (como I/O ou vazamentos de memória) durante o desenvolvimento.
 */
@Singleton
class StrictModeManager @Inject constructor(
    private val logger: Logger
) {
    /**
     * Habilita as políticas de detecção de violações. 
     * Deve ser chamado apenas em builds de DEBUG.
     */
    fun enable() {
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .penaltyFlashScreen()
                .build()
        )

        val vmPolicyBuilder = StrictMode.VmPolicy.Builder()
            .detectAll()
            .penaltyLog()

        // penaltyListener requer API 28+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            vmPolicyBuilder.penaltyListener({ it.run() }) { violation ->
                logger.e(violation, "Violação de StrictMode detectada na VM")
            }
        }

        StrictMode.setVmPolicy(vmPolicyBuilder.build())
        
        logger.i("StrictMode habilitado com sucesso.")
    }
}
