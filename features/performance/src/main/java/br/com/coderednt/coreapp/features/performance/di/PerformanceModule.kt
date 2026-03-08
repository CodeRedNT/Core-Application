package br.com.coderednt.coreapp.features.performance.di

import br.com.coderednt.coreapp.core.monitoring.analytics.AnalyticsTracker
import br.com.coderednt.coreapp.core.monitoring.performance.*
import br.com.coderednt.coreapp.features.performance.internal.AnalyticsTrackerImpl
import br.com.coderednt.coreapp.features.performance.internal.AppHealthTrackerImpl
import br.com.coderednt.coreapp.features.performance.internal.PerformanceMonitorImpl
import br.com.coderednt.coreapp.features.performance.performance.PerformanceModuleInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PerformanceModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsTracker(impl: AnalyticsTrackerImpl): AnalyticsTracker

    @Binds
    @Singleton
    abstract fun bindAppHealthTracker(impl: AppHealthTrackerImpl): AppHealthTracker

    @Binds
    @Singleton
    abstract fun bindPerformanceMonitor(impl: PerformanceMonitorImpl): PerformanceMonitor

    @Binds
    @IntoMap
    @StartupKey(PerformanceModuleInitializer::class)
    abstract fun bindPerformanceInitializer(impl: PerformanceModuleInitializer): ModuleInitializer
}
