package br.com.coderednt.coreapp.features.performance.internal

import android.os.SystemClock
import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.StartupPhase
import io.mockk.*
import org.junit.After
import org.junit.Before
import org.junit.Test

class PerformanceMonitorImplTest {

    private lateinit var monitor: PerformanceMonitorImpl
    private val appHealthTracker: AppHealthTracker = mockk(relaxed = true)

    @Before
    fun setup() {
        mockkStatic(SystemClock::class)
        monitor = PerformanceMonitorImpl(appHealthTracker)
    }

    @After
    fun tearDown() {
        unmockkStatic(SystemClock::class)
    }

    @Test
    fun `onStartActivityTracking should track activity launch delay if appEndTime is set`() {
        mockkObject(AppStartupTracker)
        // 1,000,000 nanos = 1ms
        every { AppStartupTracker.appEndTimeNanos } returns 1_000_000L 
        every { SystemClock.elapsedRealtimeNanos() } returns 5_000_000L
        
        monitor.onStartActivityTracking(5_000_000L)
        
        // 4,000_000 nanos = 4.0ms
        verify { appHealthTracker.trackPhaseTime(StartupPhase.ACTIVITY_LAUNCH, match { it >= 4.0 }) }
        
        unmockkObject(AppStartupTracker)
    }

    @Test
    fun `onTrackUiInflation should track phase time and trigger app startup tracking`() {
        mockkObject(AppStartupTracker)
        every { AppStartupTracker.isTtidReported } returns false
        every { SystemClock.elapsedRealtimeNanos() } returns 2_000_000L
        
        monitor.onTrackUiInflation(1_000_000L)
        
        // 1,000_000 nanos = 1.0ms
        verify { appHealthTracker.trackPhaseTime(StartupPhase.UI_INFLATION, match { it >= 1.0 }) }
        verify { appHealthTracker.trackAppStartup() }
        
        unmockkObject(AppStartupTracker)
    }

    @Test
    fun `onJankDetected should call trackJank on health tracker`() {
        monitor.onJankDetected("HomeScreen", 32L)
        verify { appHealthTracker.trackJank("HomeScreen") }
    }

    @Test
    fun `onTrackMemory should call trackActivityMemory with current heap usage`() {
        monitor.onTrackMemory("HomeScreen")
        verify { appHealthTracker.trackActivityMemory("HomeScreen", any()) }
    }
}
