package br.com.coderednt.coreapp.core.common.di

import br.com.coderednt.coreapp.core.common.analytics.AnalyticsTracker
import br.com.coderednt.coreapp.core.common.analytics.AnalyticsTrackerImpl
import br.com.coderednt.coreapp.core.common.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.common.performance.AppHealthTrackerImpl
import br.com.coderednt.coreapp.core.common.performance.CommonModuleInitializer
import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreCommonModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(analyticsTrackerImpl: AnalyticsTrackerImpl): AnalyticsTracker

    @Binds
    @Singleton
    abstract fun bindAppHealthTracker(appHealthTrackerImpl: AppHealthTrackerImpl): AppHealthTracker

    @Binds
    @IntoMap
    @ClassKey(CommonModuleInitializer::class)
    abstract fun bindCommonInitializer(commonModuleInitializer: CommonModuleInitializer): ModuleInitializer
}
