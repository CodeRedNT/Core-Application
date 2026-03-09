package br.com.coderednt.coreapp.core.analytics.di

import br.com.coderednt.coreapp.core.analytics.AnalyticsHelper
import br.com.coderednt.coreapp.core.analytics.DebugAnalyticsHelper
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsModule {

    @Binds
    @Singleton
    abstract fun bindAnalyticsHelper(
        debugAnalyticsHelper: DebugAnalyticsHelper
    ): AnalyticsHelper
}
