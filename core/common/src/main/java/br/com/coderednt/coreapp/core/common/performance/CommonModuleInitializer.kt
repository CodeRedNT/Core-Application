package br.com.coderednt.coreapp.core.common.performance

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "common"
    override fun initialize() {
        // Inicialização específica do core:common
    }
}
