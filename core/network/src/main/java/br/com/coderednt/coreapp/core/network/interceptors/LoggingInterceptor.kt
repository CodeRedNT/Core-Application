package br.com.coderednt.coreapp.core.network.interceptors

import br.com.coderednt.coreapp.core.logging.Logger
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException
import javax.inject.Inject

/**
 * Interceptor do OkHttp que utiliza o sistema de logging do SDK para registrar
 * detalhes de requisições e respostas HTTP.
 * 
 * @property logger A interface de logging do SDK.
 */
class LoggingInterceptor @Inject constructor(
    private val logger: Logger
) : Interceptor {

    /**
     * Intercepta a chamada de rede para logar metadados antes e depois da execução.
     */
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val t1 = System.nanoTime()

        logger.d(
            "Sending request %s on %s%n%s",
            request.url, chain.connection(), request.headers
        )

        val response = chain.proceed(request)
        val t2 = System.nanoTime()

        logger.d(
            "Received response for %s in %.1fms%n%s",
            response.request.url, (t2 - t1) / 1e6, response.headers
        )

        return response
    }
}
