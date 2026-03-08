package br.com.coderednt.coreapp

import br.com.coderednt.coreapp.core.common.base.BaseApplication
import br.com.coderednt.coreapp.core.common.performance.*
import br.com.coderednt.coreapp.core.ui.performance.UiModuleInitializer
import br.com.coderednt.coreapp.features.performance.performance.PerformanceModuleInitializer
import br.com.coderednt.coreapp.performance.AppModuleInitializer
import dagger.hilt.android.HiltAndroidApp

/**
 * Classe Application principal.
 * Em um projeto profissional multimodular, esta classe deve ser minimalista,
 * delegando inicializações para o Core e Features de forma desacoplada.
 */
@HiltAndroidApp
class CoreApplication : BaseApplication() {

    override fun onCreateModules() {
        // Inicialização explícita via DSL para total visibilidade do startup
        appHealthTracker.sync {
            module<CommonModuleInitializer>()
            module<UiModuleInitializer>()
            module<AppModuleInitializer>()
        }

        appHealthTracker.async {
            module<PerformanceModuleInitializer>()
        }
    }
}
