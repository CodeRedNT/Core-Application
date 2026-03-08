package br.com.coderednt.coreapp.navigation

import androidx.navigation.NavHostController
import br.com.coderednt.coreapp.features.performance.navigation.navigateToPerformance

class NavigationActions(private val navController: NavHostController) {
    fun navigateToPerformance() {
        navController.navigateToPerformance()
    }
}
