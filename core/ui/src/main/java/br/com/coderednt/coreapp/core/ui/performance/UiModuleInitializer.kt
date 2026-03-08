package br.com.coderednt.coreapp.core.ui.performance

import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "core:ui"
    override fun initialize() {
        // Inicialização específica do core:ui
    }
}
