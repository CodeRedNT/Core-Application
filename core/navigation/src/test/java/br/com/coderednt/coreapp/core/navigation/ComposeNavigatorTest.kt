package br.com.coderednt.coreapp.core.navigation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ComposeNavigatorTest {

    private lateinit var navigator: ComposeNavigator

    @Before
    fun setup() {
        navigator = ComposeNavigator()
    }

    @Test
    fun `navigate should emit Navigate command`() = runTest {
        val route = "home"
        navigator.navigate(route)

        val event = navigator.navigationEvents.first()
        assertTrue(event is NavigationCommand.Navigate)
        assertEquals(route, (event as NavigationCommand.Navigate).route)
    }

    @Test
    fun `navigateUp should emit NavigateUp command`() = runTest {
        navigator.navigateUp()

        val event = navigator.navigationEvents.first()
        assertTrue(event is NavigationCommand.NavigateUp)
    }
}
