package br.com.coderednt.coreapp.core.navigation

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
        
        // Usamos um dispatcher sem confinamento para capturar o evento imediatamente
        val results = mutableListOf<NavigationCommand>()
        val job = launch(UnconfinedTestDispatcher()) {
            navigator.navigationEvents.collect { results.add(it) }
        }

        navigator.navigate(route)

        assertTrue(results.first() is NavigationCommand.Navigate)
        assertEquals(route, (results.first() as NavigationCommand.Navigate).route)
        
        job.cancel()
    }

    @Test
    fun `navigateUp should emit NavigateUp command`() = runTest {
        val results = mutableListOf<NavigationCommand>()
        val job = launch(UnconfinedTestDispatcher()) {
            navigator.navigationEvents.collect { results.add(it) }
        }

        navigator.navigateUp()

        assertTrue(results.first() is NavigationCommand.NavigateUp)
        
        job.cancel()
    }
}
