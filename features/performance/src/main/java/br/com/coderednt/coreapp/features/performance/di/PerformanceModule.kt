package br.com.coderednt.coreapp.features.performance.di

import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import br.com.coderednt.coreapp.features.performance.performance.PerformanceModuleInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class PerformanceModule {

    @Binds
    @IntoMap
    @ClassKey(PerformanceModuleInitializer::class)
    abstract fun bindPerformanceInitializer(
        performanceModuleInitializer: PerformanceModuleInitializer
    ): ModuleInitializer
}
