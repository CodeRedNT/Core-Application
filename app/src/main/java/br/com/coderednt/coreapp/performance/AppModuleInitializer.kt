package br.com.coderednt.coreapp.performance

import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "app"
    override fun initialize() {
        // Simula uma carga real de inicialização (ex: analytics, crashlytics)
        // para garantir que a métrica seja capturada no dashboard.
        Thread.sleep(5) // 5ms de carga inicial
    }
}
