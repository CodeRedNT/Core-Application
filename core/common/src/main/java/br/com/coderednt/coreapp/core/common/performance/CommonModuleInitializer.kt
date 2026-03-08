package br.com.coderednt.coreapp.core.common.performance

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommonModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "core:common"
    override fun initialize() {
        // Inicialização específica do core:common
    }
}
