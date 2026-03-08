package br.com.coderednt.coreapp.core.monitoring.performance

import org.junit.Assert.assertEquals
import org.junit.Test

class MetricsModelsTest {

    @Test
    fun `BatteryMetrics dropPercentage should calculate correctly`() {
        val metrics = BatteryMetrics(
            initialLevel = 100,
            level = 95
        )
        assertEquals(5, metrics.dropPercentage)
    }

    @Test
    fun `BatteryMetrics dropPercentage should return 0 if level is higher than initial`() {
        val metrics = BatteryMetrics(
            initialLevel = 90,
            level = 95
        )
        assertEquals(0, metrics.dropPercentage)
    }

    @Test
    fun `BatteryMetrics dropPercentage should return 0 if initialLevel is not set`() {
        val metrics = BatteryMetrics(
            initialLevel = -1,
            level = 95
        )
        assertEquals(0, metrics.dropPercentage)
    }

    @Test
    fun `StartupMetrics totalStartupTimeMs should sum all phases correctly`() {
        val metrics = StartupMetrics(
            osOverheadTimeMs = 100.0,
            providerInitTimeMs = 50.0,
            diInitializationTimeMs = 200.0,
            appOnCreateTimeMs = 150.0,
            moduleLoadTimes = mapOf("mod1" to 100.0, "mod2" to 200.0),
            activityLaunchDelayMs = 50.0,
            uiInflationTimeMs = 100.0,
            splashScreenDurationMs = 50.0
        )
        
        // Sum: 100+50+200+150 + (100+200) + 50+100+50 = 1000
        assertEquals(1000.0, metrics.totalStartupTimeMs, 0.001)
    }

    @Test
    fun `HealthMetrics should initialize with default values`() {
        val metrics = HealthMetrics()
        assertEquals(0.0, metrics.startup.totalStartupTimeMs, 0.0)
        assertEquals(0, metrics.ui.jankCounts.size)
        assertEquals(0.0, metrics.memory.usedHeapMb, 0.0)
    }
}
