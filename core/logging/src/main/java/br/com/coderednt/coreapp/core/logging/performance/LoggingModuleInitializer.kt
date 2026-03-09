package br.com.coderednt.coreapp.core.logging.performance

import br.com.coderednt.coreapp.core.logging.Logger
import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LoggingModuleInitializer @Inject constructor(
    private val logger: Logger
) : ModuleInitializer {
    override val name: String = "logging"
    
    override fun initialize() {
        // Planta a árvore do Timber apenas se necessário ou realiza logs iniciais
        if (Timber.treeCount == 0) {
            Timber.plant(Timber.DebugTree())
        }
        logger.i("Módulo de Logging inicializado.")
    }
}
