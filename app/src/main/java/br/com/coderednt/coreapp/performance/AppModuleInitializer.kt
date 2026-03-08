package br.com.coderednt.coreapp.performance

import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "app"
    override fun initialize() {
        // Inicialização específica do host app
    }
}
