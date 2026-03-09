package br.com.coderednt.coreapp.core.navigation.performance

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NavigationModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "navigation"
    override fun initialize() {
        // Navigation system ready
    }
}
