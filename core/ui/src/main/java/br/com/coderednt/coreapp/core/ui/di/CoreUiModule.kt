package br.com.coderednt.coreapp.core.ui.di

import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import br.com.coderednt.coreapp.core.ui.performance.UiModuleInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CoreUiModule {

    @Binds
    @IntoMap
    @ClassKey(UiModuleInitializer::class)
    abstract fun bindUiInitializer(uiModuleInitializer: UiModuleInitializer): ModuleInitializer
}
