package br.com.coderednt.coreapp.core.datastore.performance

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "datastore"
    override fun initialize() {
        // DataStore Ready
    }
}
