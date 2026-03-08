package br.com.coderednt.coreapp.core.monitoring.di

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds

@Module
@InstallIn(SingletonComponent::class)
abstract class MonitoringModule {

    /**
     * Declara o multibinding de mapa para inicializadores.
     * Isso permite que o Hilt injete um mapa vazio caso nenhum módulo contribua com @IntoMap,
     * suportando nosso padrão Zero-Boilerplate com fallback via reflexão.
     */
    @Multibinds
    abstract fun multibindInitializers(): Map<Class<*>, ModuleInitializer>
}
