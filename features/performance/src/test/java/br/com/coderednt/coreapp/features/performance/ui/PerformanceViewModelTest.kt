package br.com.coderednt.coreapp.features.performance.ui

import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.HealthMetrics
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class PerformanceViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var viewModel: PerformanceViewModel
    private val appHealthTracker: AppHealthTracker = mockk()
    private val metricsFlow = MutableStateFlow(HealthMetrics())

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        every { appHealthTracker.metrics } returns metricsFlow
        viewModel = PerformanceViewModel(appHealthTracker)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `metricsState should reflect changes from AppHealthTracker`() = runTest {
        // Given
        val initialMetrics = viewModel.metricsState.value
        assertEquals(null, initialMetrics.lastError)

        // When: Simula uma atualização no tracker
        val updatedMetrics = HealthMetrics(lastError = "Test Error")
        metricsFlow.value = updatedMetrics

        // Then
        testDispatcher.scheduler.advanceUntilIdle()
        val newMetrics = viewModel.metricsState.value
        assertEquals("Test Error", newMetrics.lastError)
    }
}
