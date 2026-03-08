package br.com.coderednt.coreapp.features.performance.performance

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import br.com.coderednt.coreapp.features.performance.internal.ResourceMonitor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceModuleInitializer @Inject constructor(
    private val resourceMonitor: ResourceMonitor
) : ModuleInitializer {
    override val name: String = "performance"
    
    override fun initialize() {
        // Inicia o monitoramento de Recursos (Memória e Bateria)
        resourceMonitor.startMonitoring()
    }
}
