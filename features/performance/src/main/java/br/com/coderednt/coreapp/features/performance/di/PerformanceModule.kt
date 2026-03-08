package br.com.coderednt.coreapp.features.performance.di

import br.com.coderednt.coreapp.core.common.performance.PerformanceMonitor
import br.com.coderednt.coreapp.features.performance.internal.PerformanceMonitorImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PerformanceModule {

    @Binds
    @Singleton
    abstract fun bindPerformanceMonitor(
        impl: PerformanceMonitorImpl
    ): PerformanceMonitor

    // O registro do PerformanceModuleInitializer foi removido!
    // Agora o framework o encontra automaticamente via reflexão 
    // ou o desenvolvedor pode optar por não usar DI para inicializadores simples.
}
