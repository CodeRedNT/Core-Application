package br.com.coderednt.coreapp.features.performance.internal

import android.os.SystemClock
import br.com.coderednt.coreapp.core.logging.Logger
import br.com.coderednt.coreapp.core.monitoring.analytics.AnalyticsTracker
import br.com.coderednt.coreapp.core.monitoring.performance.*
import io.mockk.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import javax.inject.Provider

@OptIn(ExperimentalCoroutinesApi::class)
class AppHealthTrackerImplTest {

    private lateinit var tracker: AppHealthTrackerImpl
    private val analyticsTracker: AnalyticsTracker = mockk(relaxed = true)
    private val logger: Logger = mockk(relaxed = true)
    private val mockInitializer: ModuleInitializer = mockk(relaxed = true)
    private val mockProvider: Provider<ModuleInitializer> = mockk()

    @Before
    fun setup() {
        mockkStatic(SystemClock::class)
        every { SystemClock.elapsedRealtimeNanos() } returns 1000L
        
        every { mockProvider.get() } returns mockInitializer
        every { mockInitializer.name } returns "test_module"
        
        val initializers = mapOf<Class<out ModuleInitializer>, Provider<ModuleInitializer>>(
            mockInitializer::class.java to mockProvider
        )

        tracker = AppHealthTrackerImpl(analyticsTracker, initializers, logger)
    }

    @After
    fun tearDown() {
        unmockkStatic(SystemClock::class)
    }

    @Test
    fun `trackMemory should update memory metrics in state`() = runTest {
        val memoryMetrics = MemoryMetrics(usedHeapMb = 100.0, totalHeapMb = 512.0)
        tracker.trackMemory(memoryMetrics)
        val currentMetrics = tracker.metrics.value
        assertEquals(100.0, currentMetrics.memory.usedHeapMb, 0.1)
    }

    @Test
    fun `trackBattery should update battery metrics and preserve initial level`() = runTest {
        val battery1 = BatteryMetrics(level = 90, isCharging = false)
        tracker.trackBattery(battery1)
        assertEquals(90, tracker.metrics.value.battery.initialLevel)

        val battery2 = BatteryMetrics(level = 85, isCharging = false)
        tracker.trackBattery(battery2)
        assertEquals(90, tracker.metrics.value.battery.initialLevel)
        assertEquals(85, tracker.metrics.value.battery.level)
    }

    @Test
    fun `trackJank should increment jank count for specific screen`() = runTest {
        tracker.trackJank("HomeScreen")
        tracker.trackJank("HomeScreen")
        assertEquals(2, tracker.metrics.value.ui.jankCounts["HomeScreen"])
    }

    @Test
    fun `trackRecomposition should increment count for composable`() = runTest {
        tracker.trackRecomposition("Header")
        tracker.trackRecomposition("Header")
        assertEquals(2, tracker.metrics.value.ui.recompositionCounts["Header"])
    }

    @Test
    fun `loadModule should detect circular dependency and skip execution`() = runTest {
        val recursiveInitializer = object : ModuleInitializer {
            override val name = "circular"
            override fun initialize() { tracker.loadModule(this) }
        }
        tracker.loadModule(recursiveInitializer)
        assertNotNull(tracker.metrics.value.lastError)
    }

    @Test
    fun `loadModule should prevent re-initialization of the same module`() = runTest {
        var initCount = 0
        val counterInitializer = object : ModuleInitializer {
            override val name = "counter"
            override fun initialize() { initCount++ }
        }
        tracker.loadModule(counterInitializer)
        tracker.loadModule(counterInitializer)
        assertEquals(1, initCount)
    }

    @Test
    fun `notifyGC should increment gc counter`() = runTest {
        tracker.notifyGC()
        assertEquals(1, tracker.metrics.value.memory.gcCount)
    }
}
