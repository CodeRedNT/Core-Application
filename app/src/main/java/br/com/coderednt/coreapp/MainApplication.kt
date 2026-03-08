package br.com.coderednt.coreapp

import br.com.coderednt.coreapp.core.common.base.BaseApplication
import br.com.coderednt.coreapp.core.common.performance.CommonModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.*
import br.com.coderednt.coreapp.core.ui.performance.UiModuleInitializer
import br.com.coderednt.coreapp.features.performance.performance.PerformanceModuleInitializer
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe de aplicação principal que inicializa o grafo de dependências do Hilt
 * e configura os módulos do sistema através do AppHealthTracker.
 */
@HiltAndroidApp
class MainApplication : BaseApplication() {

    /**
     * Define a ordem de inicialização dos módulos do aplicativo.
     * Módulos críticos para o monitoramento e UI base são carregados de forma síncrona.
     */
    override fun onCreateModules() {
        appHealthTracker.sync {
            module<MonitoringModuleInitializer>()
            module<CommonModuleInitializer>()
            module<UiModuleInitializer>()
            module<PerformanceModuleInitializer>()
        }

        appHealthTracker.async {
            // Reservado para futuros módulos assíncronos
        }
    }
}
