package br.com.coderednt.coreapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.createComposeRule
import org.junit.Assert.assertNotNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class ThemeTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun theme_should_provide_colors() {
        composeTestRule.setContent {
            CoreAppTheme {
                val colorScheme = MaterialTheme.colorScheme
                assertNotNull(colorScheme.primary)
                assertNotNull(colorScheme.secondary)
                assertNotNull(colorScheme.background)
            }
        }
    }

    @Test
    fun theme_should_provide_typography() {
        composeTestRule.setContent {
            CoreAppTheme {
                val typography = MaterialTheme.typography
                assertNotNull(typography.headlineMedium)
                assertNotNull(typography.bodyLarge)
            }
        }
    }

    @Test
    fun theme_should_provide_shapes() {
        composeTestRule.setContent {
            CoreAppTheme {
                val shapes = MaterialTheme.shapes
                assertNotNull(shapes.medium)
            }
        }
    }
}
