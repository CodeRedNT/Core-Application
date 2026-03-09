package br.com.coderednt.coreapp.core.database.di

import android.content.Context
import androidx.room.Room
import br.com.coderednt.coreapp.core.database.AppDatabase
import br.com.coderednt.coreapp.core.database.performance.DatabaseModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.StartupKey
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import dagger.multibindings.IntoMap
import javax.inject.Singleton

/**
 * Módulo Hilt responsável pela provisão das dependências de persistência.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    /**
     * Vincula o inicializador do banco de dados ao mapa de inicializadores do SDK.
     */
    @Binds
    @IntoMap
    @StartupKey(DatabaseModuleInitializer::class)
    abstract fun bindDatabaseInitializer(impl: DatabaseModuleInitializer): ModuleInitializer

    companion object {
        /**
         * Provê a instância Singleton do [AppDatabase].
         */
        @Provides
        @Singleton
        fun provideAppDatabase(
            @ApplicationContext context: Context
        ): AppDatabase {
            return Room.databaseBuilder(
                context,
                AppDatabase::class.java,
                "core-app-database"
            ).build()
        }
    }
}
