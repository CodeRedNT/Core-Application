package br.com.coderednt.coreapp.core.network.di

import br.com.coderednt.coreapp.core.logging.Logger
import br.com.coderednt.coreapp.core.network.interceptors.LoggingInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * Módulo Hilt responsável por fornecer as dependências de rede globais.
 * Configura o cliente HTTP (OkHttp), o serializador JSON (Moshi) e o cliente REST (Retrofit).
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Fornece uma instância singleton do [Moshi] configurada com suporte a Kotlin.
     */
    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    /**
     * Fornece o [LoggingInterceptor] customizado para logar requisições de rede.
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(logger: Logger): LoggingInterceptor =
        LoggingInterceptor(logger)

    /**
     * Fornece uma instância singleton do [OkHttpClient] configurada com timeouts padrão
     * e interceptores de logging.
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: LoggingInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    /**
     * Fornece a instância principal do [Retrofit].
     * 
     * @param okHttpClient O cliente HTTP configurado.
     * @param moshi O conversor JSON para serialização/desserialização.
     */
    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi
    ): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.example.com/") // URL base padrão, deve ser customizada via build flavors ou config
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .build()
}
