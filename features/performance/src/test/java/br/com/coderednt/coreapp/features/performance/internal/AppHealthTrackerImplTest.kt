package br.com.coderednt.coreapp.features.performance.internal

import android.os.SystemClock
import android.util.Log
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
    private val mockInitializer: ModuleInitializer = mockk(relaxed = true)
    private val mockProvider: Provider<ModuleInitializer> = mockk()

    @Before
    fun setup() {
        mockkStatic(SystemClock::class)
        mockkStatic(Log::class)
        
        every { Log.d(any(), any()) } returns 0
        every { Log.e(any(), any()) } returns 0
        every { Log.e(any(), any(), any()) } returns 0
        
        every { SystemClock.elapsedRealtimeNanos() } returns 1000L
        
        every { mockProvider.get() } returns mockInitializer
        every { mockInitializer.name } returns "test_module"
        
        val initializers = mapOf<Class<out ModuleInitializer>, Provider<ModuleInitializer>>(
            mockInitializer::class.java to mockProvider
        )

        tracker = AppHealthTrackerImpl(analyticsTracker, initializers)
    }

    @After
    fun tearDown() {
        unmockkStatic(SystemClock::class)
        unmockkStatic(Log::class)
    }

    @Test
    fun `trackMemory should update memory metrics in state`() = runTest {
        val memoryMetrics = MemoryMetrics(usedHeapMb = 100.0, totalHeapMb = 512.0)
        
        tracker.trackMemory(memoryMetrics)
        
        val currentMetrics = tracker.metrics.value
        assertEquals(100.0, currentMetrics.memory.usedHeapMb, 0.1)
        assertEquals(512.0, currentMetrics.memory.totalHeapMb, 0.1)
    }

    @Test
    fun `trackBattery should update battery metrics and preserve initial level`() = runTest {
        val battery1 = BatteryMetrics(level = 90, isCharging = false)
        tracker.trackBattery(battery1)
        
        assertEquals(90, tracker.metrics.value.battery.initialLevel)
        assertEquals(90, tracker.metrics.value.battery.level)

        val battery2 = BatteryMetrics(level = 85, isCharging = false)
        tracker.trackBattery(battery2)

        assertEquals(90, tracker.metrics.value.battery.initialLevel)
        assertEquals(85, tracker.metrics.value.battery.level)
        assertEquals(5, tracker.metrics.value.battery.dropPercentage)
    }

    @Test
    fun `trackJank should increment jank count for specific screen`() = runTest {
        tracker.trackJank("HomeScreen")
        tracker.trackJank("HomeScreen")
        tracker.trackJank("DetailScreen")

        val uiMetrics = tracker.metrics.value.ui
        assertEquals(2, uiMetrics.jankCounts["HomeScreen"])
        assertEquals(1, uiMetrics.jankCounts["DetailScreen"])
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
            override fun initialize() {
                tracker.loadModule(this)
            }
        }

        tracker.loadModule(recursiveInitializer)

        assertNotNull(tracker.metrics.value.lastError)
        assertTrue(tracker.metrics.value.lastError!!.contains("Ciclo detectado"))
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
    fun `trackError should store last error message`() = runTest {
        tracker.trackError("NullPointerException at line 42")
        assertEquals("NullPointerException at line 42", tracker.metrics.value.lastError)
    }

    @Test
    fun `notifyGC should increment gc counter`() = runTest {
        tracker.notifyGC()
        tracker.notifyGC()
        assertEquals(2, tracker.metrics.value.memory.gcCount)
    }
}
