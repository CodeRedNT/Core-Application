package br.com.coderednt.coreapp.core.common.performance

import br.com.coderednt.coreapp.core.common.analytics.AnalyticsTracker
import br.com.coderednt.coreapp.core.common.analytics.AnalyticsTrackerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.Multibinds
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

    /**
     * Declara o multibinding de mapa para inicializadores.
     * Isso permite que o Hilt injete um mapa vazio caso nenhum módulo contribua com @IntoMap,
     * suportando nosso padrão Zero-Boilerplate com fallback via reflexão.
     */
    @Multibinds
    abstract fun multibindInitializers(): Map<Class<*>, ModuleInitializer>
}
