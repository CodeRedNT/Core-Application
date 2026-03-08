package br.com.coderednt.coreapp.features.performance.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import br.com.coderednt.coreapp.features.performance.ui.PerformanceDashboardRoute

const val PERFORMANCE_ROUTE = "performance_route"

fun NavController.navigateToPerformance(navOptions: NavOptions? = null) {
    this.navigate(PERFORMANCE_ROUTE, navOptions)
}

fun NavGraphBuilder.performanceScreen() {
    composable(route = PERFORMANCE_ROUTE) {
        PerformanceDashboardRoute()
    }
}
