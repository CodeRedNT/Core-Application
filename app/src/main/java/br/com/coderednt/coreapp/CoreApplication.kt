package br.com.coderednt.coreapp

import br.com.coderednt.coreapp.core.common.base.BaseApplication
import br.com.coderednt.coreapp.core.common.performance.*
import br.com.coderednt.coreapp.core.ui.performance.UiModuleInitializer
import br.com.coderednt.coreapp.features.performance.performance.PerformanceModuleInitializer
import br.com.coderednt.coreapp.performance.AppModuleInitializer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CoreApplication : BaseApplication() {

    override fun AppHealthTracker.onCreateModules() {
        sync {
            module<CommonModuleInitializer>()
            module<UiModuleInitializer>()
            module<AppModuleInitializer>()
        }
        
        async {
            module<PerformanceModuleInitializer>()
        }
    }
}
