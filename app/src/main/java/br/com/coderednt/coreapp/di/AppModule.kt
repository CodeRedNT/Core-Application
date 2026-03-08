package br.com.coderednt.coreapp.di

import br.com.coderednt.coreapp.core.common.performance.ModuleInitializer
import br.com.coderednt.coreapp.performance.AppModuleInitializer
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.ClassKey
import dagger.multibindings.IntoMap

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    @IntoMap
    @ClassKey(AppModuleInitializer::class)
    abstract fun bindAppInitializer(appModuleInitializer: AppModuleInitializer): ModuleInitializer
}
