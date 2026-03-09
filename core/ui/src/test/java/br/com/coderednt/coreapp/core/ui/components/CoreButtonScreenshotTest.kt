package br.com.coderednt.coreapp.core.ui.components

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import br.com.coderednt.coreapp.core.ui.theme.CoreAppTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33])
class CoreButtonScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun coreButton_lightTheme() {
        composeTestRule.setContent {
            CoreAppTheme(darkTheme = false) {
                CoreButton(
                    text = "Test Button",
                    onClick = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage("core_button_light.png")
    }

    @Test
    fun coreButton_darkTheme() {
        composeTestRule.setContent {
            CoreAppTheme(darkTheme = true) {
                CoreButton(
                    text = "Test Button",
                    onClick = {}
                )
            }
        }

        composeTestRule.onRoot().captureRoboImage("core_button_dark.png")
    }
}
