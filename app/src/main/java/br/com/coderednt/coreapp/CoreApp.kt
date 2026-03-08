package br.com.coderednt.coreapp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.coderednt.coreapp.core.ui.theme.CoreAppTheme
import br.com.coderednt.coreapp.features.performance.navigation.NavigationObserver
import br.com.coderednt.coreapp.features.performance.navigation.PERFORMANCE_ROUTE
import br.com.coderednt.coreapp.navigation.CoreNavHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Orquestrador principal da UI do aplicativo.
 * Responsável apenas pela estrutura de Scaffold e Navegação de alto nível.
 */
@Composable
fun CoreApp() {
    CoreAppTheme {
        val navController = rememberNavController()
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination
        
        // Injeta o observador de performance para navegação
        val navigationViewModel: PerformanceNavigationViewModel = hiltViewModel()
        val navigationObserver = navigationViewModel.observer

        // Instala o observador no NavController
        LaunchedEffect(navController) {
            navigationObserver.install(navController)
        }

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home") },
                        selected = currentDestination?.hierarchy?.any { it.route == "games_route" } == true,
                        onClick = {
                            navigationObserver.markStart()
                            navController.navigate("games_route") {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Speed, contentDescription = null) },
                        label = { Text("Performance") },
                        selected = currentDestination?.hierarchy?.any { it.route == PERFORMANCE_ROUTE } == true,
                        onClick = {
                            navigationObserver.markStart()
                            navController.navigate(PERFORMANCE_ROUTE) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            CoreNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

/**
 * ViewModel auxiliar para prover o NavigationObserver via Hilt no Compose.
 */
@HiltViewModel
class PerformanceNavigationViewModel @Inject constructor(
    val observer: NavigationObserver
) : ViewModel()
