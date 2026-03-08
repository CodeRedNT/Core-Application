package br.com.coderednt.coreapp.core.monitoring.di

import br.com.coderednt.coreapp.core.monitoring.performance.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringModule {

    @Multibinds
    abstract fun multibindInitializers(): Map<Class<*>, ModuleInitializer>

    @Binds
    @IntoMap
    @StartupKey(MonitoringModuleInitializer::class)
    abstract fun bindMonitoringInitializer(impl: MonitoringModuleInitializer): ModuleInitializer
}
