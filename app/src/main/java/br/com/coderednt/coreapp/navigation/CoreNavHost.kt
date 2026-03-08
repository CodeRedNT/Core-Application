package br.com.coderednt.coreapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import br.com.coderednt.coreapp.features.performance.navigation.PERFORMANCE_ROUTE
import br.com.coderednt.coreapp.features.performance.navigation.performanceScreen

@Composable
fun CoreNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    startDestination: String = "games_route"
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier
    ) {
        composable("games_route") {
            // Placeholder for the external Games module
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Games List (External Module Placeholder)")
            }
        }

        performanceScreen()
        // Add other feature screens here
    }
}
