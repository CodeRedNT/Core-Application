package br.com.coderednt.coreapp.core.ui.di

import br.com.coderednt.coreapp.core.ui.performance.UiModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class UiModule {

    @Binds
    @IntoMap
    @StartupKey(UiModuleInitializer::class)
    abstract fun bindUiInitializer(impl: UiModuleInitializer): ModuleInitializer
}
