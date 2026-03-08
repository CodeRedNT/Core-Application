package br.com.coderednt.coreapp

import br.com.coderednt.coreapp.core.common.base.BaseApplication
import br.com.coderednt.coreapp.core.common.performance.*
import br.com.coderednt.coreapp.core.ui.performance.UiModuleInitializer
import br.com.coderednt.coreapp.features.performance.performance.PerformanceModuleInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : BaseApplication() {

    override fun onCreateModules() {
        appHealthTracker.sync {
            module<CommonModuleInitializer>()
            module<UiModuleInitializer>()
        }

        appHealthTracker.async {
            module<PerformanceModuleInitializer>()
        }
    }
}
