package br.com.coderednt.coreapp.core.analytics.di

import br.com.coderednt.coreapp.core.analytics.AnalyticsHelper
import br.com.coderednt.coreapp.core.analytics.DebugAnalyticsHelper
import br.com.coderednt.coreapp.core.analytics.performance.AnalyticsModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.StartupKey
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @IntoMap
    @StartupKey(AnalyticsModuleInitializer::class)
    abstract fun bindAnalyticsInitializer(impl: AnalyticsModuleInitializer): ModuleInitializer

    @Binds
    @Singleton
    abstract fun bindAnalyticsHelper(
        debugAnalyticsHelper: DebugAnalyticsHelper
    ): AnalyticsHelper
}
