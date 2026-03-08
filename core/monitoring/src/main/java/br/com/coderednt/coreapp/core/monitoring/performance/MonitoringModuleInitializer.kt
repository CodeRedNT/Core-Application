package br.com.coderednt.coreapp.core.monitoring.performance

import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inicializador central para o módulo de Monitoring.
 * Responsável por garantir que a infraestrutura de métricas esteja pronta.
 */
@Singleton
class MonitoringModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "monitoring"
    
    override fun initialize() {
        // Lógica de inicialização do core de monitoramento (ex: Analytics, Crashlytics)
    }
}
