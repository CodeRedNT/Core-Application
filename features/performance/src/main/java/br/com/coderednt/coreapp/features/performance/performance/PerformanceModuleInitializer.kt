package br.com.coderednt.coreapp.features.performance.performance

import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "feature:performance"
    override val isParallel: Boolean = true // Features podem ser carregadas em paralelo por padrão
    override fun initialize() {
        // Inicialização específica da feature de performance
    }
}
