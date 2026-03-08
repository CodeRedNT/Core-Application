package br.com.coderednt.coreapp.features.performance.ui

import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.HealthMetrics
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class PerformanceViewModelTest {

    private lateinit var viewModel: PerformanceViewModel
    private val appHealthTracker: AppHealthTracker = mockk()
    private val metricsFlow = MutableStateFlow(HealthMetrics())

    @Before
    fun setup() {
        every { appHealthTracker.metrics } returns metricsFlow
        viewModel = PerformanceViewModel(appHealthTracker)
    }

    @Test
    fun `uiState should reflect changes from AppHealthTracker metrics`() {
        val initialMetrics = viewModel.uiState.value
        assertEquals(0.0, initialMetrics.startup.totalStartupTimeMs, 0.0)

        // Simula uma atualização nas métricas
        val updatedMetrics = HealthMetrics(lastError = "Test Error")
        metricsFlow.value = updatedMetrics

        assertEquals("Test Error", viewModel.uiState.value.lastError)
    }
}
