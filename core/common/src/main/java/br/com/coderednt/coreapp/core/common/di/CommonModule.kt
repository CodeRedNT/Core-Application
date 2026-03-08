package br.com.coderednt.coreapp.core.common.di

import br.com.coderednt.coreapp.core.common.performance.CommonModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class CommonModule {

    @Binds
    @IntoMap
    @StartupKey(CommonModuleInitializer::class)
    abstract fun bindCommonInitializer(impl: CommonModuleInitializer): ModuleInitializer
}
