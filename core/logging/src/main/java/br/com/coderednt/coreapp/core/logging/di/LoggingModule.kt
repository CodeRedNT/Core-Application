package br.com.coderednt.coreapp.core.logging.di

import br.com.coderednt.coreapp.core.logging.Logger
import br.com.coderednt.coreapp.core.logging.LoggerImpl
import br.com.coderednt.coreapp.core.logging.performance.LoggingModuleInitializer
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
abstract class LoggingModule {

    @Binds
    @Singleton
    abstract fun bindLogger(impl: LoggerImpl): Logger

    @Binds
    @IntoMap
    @StartupKey(LoggingModuleInitializer::class)
    abstract fun bindLoggingInitializer(impl: LoggingModuleInitializer): ModuleInitializer
}
