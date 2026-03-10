package br.com.coderednt.coreapp.core.logging

import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import dagger.Lazy
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import io.mockk.verify
import org.junit.After
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class LoggerImplTest {

    private val healthTracker: AppHealthTracker = mockk(relaxed = true)
    private val lazyHealthTracker: Lazy<AppHealthTracker> = mockk {
        every { get() } returns healthTracker
    }
    private lateinit var logger: LoggerImpl

    @Before
    fun setUp() {
        mockkStatic(Timber::class)
        logger = LoggerImpl(lazyHealthTracker)
    }

    @After
    fun tearDown() {
        unmockkStatic(Timber::class)
    }

    @Test
    fun `d should call Timber d`() {
        // Given
        // Correct way to match varargs in static methods with MockK when using any()
        every { Timber.d(any<String>(), *anyVararg()) } returns Unit
        
        // When
        logger.d("test message")
        
        // Then
        verify { Timber.d(eq("test message"), *anyVararg()) }
    }

    @Test
    fun `i should call Timber i`() {
        every { Timber.i(any<String>(), *anyVararg()) } returns Unit
        logger.i("test message")
        verify { Timber.i(eq("test message"), *anyVararg()) }
    }

    @Test
    fun `w should call Timber w`() {
        every { Timber.w(any<String>(), *anyVararg()) } returns Unit
        logger.w("test message")
        verify { Timber.w(eq("test message"), *anyVararg()) }
    }

    @Test
    fun `e should call Timber e`() {
        val throwable = RuntimeException()
        every { Timber.e(any<Throwable>(), any<String>(), *anyVararg()) } returns Unit
        
        logger.e(throwable, "test error")
        
        verify { Timber.e(eq(throwable), eq("test error"), *anyVararg()) }
    }

    @Test
    fun `logAndTrack should log error and track in health tracker`() {
        val throwable = RuntimeException()
        val message = "critical error %s"
        val arg = "param"
        
        every { Timber.e(any<Throwable>(), any<String>(), *anyVararg()) } returns Unit
        
        logger.logAndTrack(throwable, message, arg)

        verify { Timber.e(eq(throwable), eq(message), *anyVararg()) }
        verify { healthTracker.trackError("critical error param") }
    }
}
