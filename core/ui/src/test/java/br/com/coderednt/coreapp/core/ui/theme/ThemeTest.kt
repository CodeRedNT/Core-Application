package br.com.coderednt.coreapp.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
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
    fun `theme should provide material typography`() {
        composeTestRule.setContent {
            CoreAppTheme {
                Text(
                    text = "Theme Test",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }

        composeTestRule.onNodeWithText("Theme Test").assertExists()
    }
}
