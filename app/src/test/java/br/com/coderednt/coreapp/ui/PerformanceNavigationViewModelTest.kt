package br.com.coderednt.coreapp.ui

import br.com.coderednt.coreapp.features.performance.navigation.NavigationObserver
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

class PerformanceNavigationViewModelTest {

    @Test
    fun `viewModel should provide injected observer`() {
        val mockObserver = mockk<NavigationObserver>()
        val viewModel = PerformanceNavigationViewModel(mockObserver)
        
        assertEquals(mockObserver, viewModel.observer)
    }
}
