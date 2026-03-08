package br.com.coderednt.coreapp.features.performance.performance

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "performance"
    override fun initialize() {
        // O módulo de performance agora herda o papel do antigo MonitoringModuleInitializer
        // Centralizando a inicialização de monitoramento aqui.
    }
}
