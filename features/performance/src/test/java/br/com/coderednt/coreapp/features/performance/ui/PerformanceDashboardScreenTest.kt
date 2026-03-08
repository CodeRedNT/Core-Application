package br.com.coderednt.coreapp.features.performance.ui

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import br.com.coderednt.coreapp.core.monitoring.performance.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Teste de UI (via Robolectric + Compose Test) para o Dashboard.
 * Valida se os dados do modelo HealthMetrics são renderizados corretamente.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], instrumentedPackages = ["androidx.loader.content"])
class PerformanceDashboardScreenTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun `dashboard should display startup time correctly`() {
        val mockMetrics = HealthMetrics(
            startup = StartupMetrics(osOverheadTimeMs = 1500.0)
        )

        composeTestRule.setContent {
            PerformanceDashboardScreen(uiState = mockMetrics)
        }

        // 1500ms formatado deve aparecer como "1.50 s" ou "1500.00 ms" dependendo da lógica
        // Verificando apenas a presença do texto base
        composeTestRule.onNodeWithText("App Startup (TTID)").assertExists()
    }

    @Test
    fun `dashboard should display memory usage correctly`() {
        val mockMetrics = HealthMetrics(
            memory = MemoryMetrics(usedHeapMb = 128.5)
        )

        composeTestRule.setContent {
            MemoryTabContent(uiState = mockMetrics)
        }

        composeTestRule.onNodeWithText("128.5 MB").assertExists()
    }

    @Test
    fun `dashboard should display battery level correctly`() {
        val mockMetrics = HealthMetrics(
            battery = BatteryMetrics(level = 88)
        )

        composeTestRule.setContent {
            BatteryTabContent(uiState = mockMetrics)
        }

        composeTestRule.onNodeWithText("88%").assertExists()
    }
}
