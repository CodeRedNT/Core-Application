package br.com.coderednt.coreapp

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import br.com.coderednt.coreapp.core.common.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.ui.theme.CoreAppTheme
import br.com.coderednt.coreapp.features.performance.navigation.PERFORMANCE_ROUTE
import br.com.coderednt.coreapp.navigation.CoreNavHost
import br.com.coderednt.coreapp.navigation.NavigationActions
import javax.inject.Inject

@Composable
fun CoreApp(
    appHealthTracker: AppHealthTracker
) {
    CoreAppTheme {
        val navController = rememberNavController()
        val navActions = remember(navController) {
            NavigationActions(navController)
        }

        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        // --- TRACKING DE NAVEGAÇÃO ---
        var navigationStartTime by remember { mutableLongStateOf(0L) }
        
        LaunchedEffect(navBackStackEntry) {
            navBackStackEntry?.let { entry ->
                if (navigationStartTime > 0) {
                    val duration = System.currentTimeMillis() - navigationStartTime
                    val routeName = entry.destination.route ?: "unknown"
                    appHealthTracker.trackRenderTime("Nav: $routeName", duration)
                    navigationStartTime = 0L // Reset
                }
            }
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
                            navigationStartTime = System.currentTimeMillis()
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
                            navigationStartTime = System.currentTimeMillis()
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
