package br.com.coderednt.coreapp.features.performance.performance

import android.app.Application
import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import br.com.coderednt.coreapp.features.performance.internal.PerformanceActivityLifecycleCallbacks
import br.com.coderednt.coreapp.features.performance.internal.ResourceMonitor
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceModuleInitializer @Inject constructor(
    private val application: Application,
    private val resourceMonitor: ResourceMonitor,
    private val lifecycleCallbacks: PerformanceActivityLifecycleCallbacks
) : ModuleInitializer {
    override val name: String = "performance"
    
    override fun initialize() {
        // Inicia o monitoramento de Recursos (Memória e Bateria)
        resourceMonitor.startMonitoring()
        
        // Registra o monitoramento automático de Activities
        application.registerActivityLifecycleCallbacks(lifecycleCallbacks)
    }
}
