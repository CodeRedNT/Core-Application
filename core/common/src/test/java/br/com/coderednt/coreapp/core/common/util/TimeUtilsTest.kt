package br.com.coderednt.coreapp.core.common.util

import android.os.SystemClock
import io.mockk.every
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class TimeUtilsTest {

    @Before
    fun setup() {
        mockkStatic(SystemClock::class)
    }

    @After
    fun tearDown() {
        unmockkStatic(SystemClock::class)
    }

    @Test
    fun `nowNanos should return value from SystemClock`() {
        val expectedNanos = 123456789L
        every { SystemClock.elapsedRealtimeNanos() } returns expectedNanos
        
        assertEquals(expectedNanos, TimeUtils.nowNanos())
    }

    @Test
    fun `nanosToMillis should convert correctly`() {
        val nanos = 1_000_000L
        val expectedMillis = 1.0
        
        assertEquals(expectedMillis, TimeUtils.nanosToMillis(nanos), 0.001)
    }

    @Test
    fun `calculateDurationFrom should return difference in millis`() {
        val startNanos = 100_000_000L
        val currentNanos = 250_000_000L // 150ms depois
        
        every { SystemClock.elapsedRealtimeNanos() } returns currentNanos
        
        val duration = TimeUtils.calculateDurationFrom(startNanos)
        assertEquals(150.0, duration, 0.001)
    }

    @Test
    fun `calculateDurationFrom should return 0 if start is 0 or negative`() {
        assertEquals(0.0, TimeUtils.calculateDurationFrom(0), 0.0)
        assertEquals(0.0, TimeUtils.calculateDurationFrom(-100), 0.0)
    }
}
