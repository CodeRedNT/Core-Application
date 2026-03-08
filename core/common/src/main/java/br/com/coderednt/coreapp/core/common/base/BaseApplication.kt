package br.com.coderednt.coreapp.core.common.base

import android.app.Application
import br.com.coderednt.coreapp.core.common.util.TimeUtils
import br.com.coderednt.coreapp.core.monitoring.performance.*
import javax.inject.Inject
import kotlin.system.exitProcess

/**
 * BaseApplication abstrai a lógica de inicialização, tratamento de erros global
 * e rastreamento de saúde do aplicativo.
 */
abstract class BaseApplication : Application() {

    @Inject
    lateinit var appHealthTracker: AppHealthTracker

    override fun onCreate() {
        // 1. Marca o início real do método usando a padronização de tempo
        val startOnCreate = TimeUtils.nowNanos()
        
        // 2. super.onCreate() é onde o Hilt realiza a injeção de membros.
        super.onCreate()
        
        // 3. Agora que o appHealthTracker foi injetado, calculamos o tempo gasto no super
        val diDurationMs = TimeUtils.calculateDurationFrom(startOnCreate)
        
        setupErrorHandling()
        
        // 4. Reportamos a fase de DI (Hilt Init)
        appHealthTracker.onAppStart()
        appHealthTracker.trackPhaseTime(StartupPhase.DI_INIT, diDurationMs)
        
        onCreateModules()

        appHealthTracker.onAppEnd()
    }

    private fun setupErrorHandling() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (::appHealthTracker.isInitialized) {
                val errorMessage = "Crash in ${thread.name}: ${throwable.localizedMessage ?: "Unknown Error"}"
                appHealthTracker.trackError(errorMessage)
            }
            defaultHandler?.uncaughtException(thread, throwable) ?: exitProcess(1)
        }
    }

    abstract fun onCreateModules()
}
