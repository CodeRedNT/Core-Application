package br.com.coderednt.coreapp.core.network.interceptors

import br.com.coderednt.coreapp.core.logging.Logger
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import okhttp3.Interceptor
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import org.junit.Before
import org.junit.Test

class LoggingInterceptorTest {

    private val logger: Logger = mockk(relaxed = true)
    private lateinit var interceptor: LoggingInterceptor
    private val chain: Interceptor.Chain = mockk()

    @Before
    fun setUp() {
        interceptor = LoggingInterceptor(logger)
    }

    @Test
    fun `intercept should log request and response`() {
        // Given
        val request = Request.Builder()
            .url("https://api.example.com/test")
            .build()
        
        val response = Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(200)
            .message("OK")
            .build()

        every { chain.request() } returns request
        every { chain.connection() } returns null
        every { chain.proceed(request) } returns response

        // When
        interceptor.intercept(chain)

        // Then
        verify { 
            logger.d(match { it.contains("Sending request") }, any(), any(), any())
            logger.d(match { it.contains("Received response") }, any(), any(), any())
        }
    }
}
