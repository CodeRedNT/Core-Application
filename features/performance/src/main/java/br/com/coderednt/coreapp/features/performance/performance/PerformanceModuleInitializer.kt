package br.com.coderednt.coreapp.features.performance.performance

import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "performance"

    override fun initialize() {
        // Inicialização específica da feature de performance
    }
}
