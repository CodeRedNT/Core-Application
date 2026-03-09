package br.com.coderednt.coreapp.core.architecture

import android.app.Application
import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import javax.inject.Inject
import kotlin.system.exitProcess

/**
 * Classe Application base que gerencia o ciclo de vida global, 
 * tratamento de erros não capturados e métricas de inicialização.
 */
abstract class BaseApplication : Application() {

    @Inject
    lateinit var appHealthTracker: AppHealthTracker

    override fun onCreate() {
        val startOnCreate = System.nanoTime()
        
        // super.onCreate() dispara a injeção do Hilt em classes que estendem esta.
        super.onCreate()
        
        val diDurationMs = (System.nanoTime() - startOnCreate) / 1_000_000.0
        
        setupGlobalErrorHandling()
        
        // Notifica o início do rastreamento de saúde do app
        appHealthTracker.onAppStart()
        appHealthTracker.trackPhaseTime(StartupPhase.DI_INIT, diDurationMs)
        
        // Hook para inicialização de módulos específicos do app
        onCreateModules()

        appHealthTracker.onAppEnd()
    }

    /**
     * Configura um handler global para capturar exceções não tratadas e 
     * reportá-las via AppHealthTracker antes do encerramento do processo.
     */
    private fun setupGlobalErrorHandling() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            if (::appHealthTracker.isInitialized) {
                val errorMessage = "Crash in thread [${thread.name}]: ${throwable.localizedMessage ?: "Unknown Error"}"
                appHealthTracker.trackError(errorMessage)
            }
            // Delega para o handler padrão do sistema para manter o comportamento esperado (crash dialog)
            defaultHandler?.uncaughtException(thread, throwable) ?: exitProcess(1)
        }
    }

    /**
     * Método abstrato para inicialização de módulos e dependências específicas.
     */
    abstract fun onCreateModules()
}
