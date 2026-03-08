package br.com.coderednt.coreapp.core.common.base

import android.app.Application
import br.com.coderednt.coreapp.core.monitoring.performance.*
import javax.inject.Inject
import kotlin.system.exitProcess

abstract class BaseApplication : Application() {

    @Inject
    lateinit var appHealthTracker: AppHealthTracker

    override fun onCreate() {
        setupErrorHandling()
        
        // Marca o início absoluto do Application.onCreate via interface desacoplada
        appHealthTracker.onAppStart()

        // Medimos o custo do super.onCreate (Hilt Overhead / DI Init)
        val diStart = System.nanoTime()
        super.onCreate()
        val diDuration = (System.nanoTime() - diStart) / 1_000_000.0

        // Registra explicitamente o tempo do DI para compor as fases
        appHealthTracker.trackPhaseTime(StartupPhase.DI_INIT, diDuration)

        // Inicialização de módulos via DSL
        onCreateModules()

        // Marca o fim absoluto do Application.onCreate via interface desacoplada
        appHealthTracker.onAppEnd()
    }

    private fun setupErrorHandling() {
        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()
        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            val errorMessage = "Crash in ${thread.name}: ${throwable.localizedMessage ?: "Unknown Error"}"
            appHealthTracker.trackError(errorMessage)
            
            // Permite que o sistema trate o crash após o log
            defaultHandler?.uncaughtException(thread, throwable) ?: exitProcess(1)
        }
    }

    /**
     * Define os módulos a serem carregados durante o startup.
     * Deve ser implementado pela classe Application final.
     */
    abstract fun onCreateModules()
}
