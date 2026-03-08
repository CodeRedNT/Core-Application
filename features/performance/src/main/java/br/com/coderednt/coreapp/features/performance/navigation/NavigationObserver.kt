package br.com.coderednt.coreapp.features.performance.navigation

import androidx.navigation.NavController
import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import javax.inject.Inject

/**
 * Observador de navegação para métricas de performance.
 */
class NavigationObserver @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) {
    private var lastNavigationStartTime: Long = 0L

    fun install(navController: NavController) {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (lastNavigationStartTime > 0) {
                val duration = System.currentTimeMillis() - lastNavigationStartTime
                val route = destination.route ?: "unknown"
                appHealthTracker.trackNavigationTime("Nav: $route", duration)
                lastNavigationStartTime = 0L
            }
        }
    }

    fun markStart() {
        lastNavigationStartTime = System.currentTimeMillis()
    }
}
