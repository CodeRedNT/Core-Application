package br.com.coderednt.coreapp.core.logging

import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import dagger.Lazy
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class LoggerTest {

    private val healthTracker: AppHealthTracker = mockk(relaxed = true)
    private val lazyHealthTracker: Lazy<AppHealthTracker> = mockk {
        every { get() } returns healthTracker
    }
    private lateinit var logger: LoggerImpl

    @Before
    fun setup() {
        logger = LoggerImpl(lazyHealthTracker)
    }

    @Test
    fun `logAndTrack should notify health tracker`() {
        val errorMessage = "Critical Failure"
        
        logger.logAndTrack(message = errorMessage)

        verify { healthTracker.trackError(errorMessage) }
    }

    @Test
    fun `logAndTrack with args should notify health tracker with formatted message`() {
        val messageTemplate = "Failure in %s"
        val arg = "Database"
        val expectedMessage = "Failure in Database"

        logger.logAndTrack(message = messageTemplate, args = arrayOf(arg))

        verify { healthTracker.trackError(expectedMessage) }
    }
}
