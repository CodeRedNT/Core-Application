package br.com.coderednt.coreapp.core.analytics.performance

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AnalyticsModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "analytics"
    override fun initialize() {
        // Analytics init logic
    }
}
