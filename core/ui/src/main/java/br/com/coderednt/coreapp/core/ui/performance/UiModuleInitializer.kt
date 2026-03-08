package br.com.coderednt.coreapp.core.ui.performance

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UiModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "ui"
    override fun initialize() {
        // Inicialização específica do core:ui
    }
}
