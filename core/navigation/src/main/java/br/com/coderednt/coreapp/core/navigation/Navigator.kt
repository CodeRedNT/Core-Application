package br.com.coderednt.coreapp.core.navigation

import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface para gerenciar a navegação de forma desacoplada.
 */
interface Navigator {
    val navigationEvents: SharedFlow<NavigationCommand>
    
    fun navigate(
        route: String,
        builder: NavigationOptions.() -> Unit = {}
    )
    
    fun navigateUp()
}

sealed class NavigationCommand {
    data class Navigate(
        val route: String,
        val options: NavigationOptions = NavigationOptions()
    ) : NavigationCommand()
    
    object NavigateUp : NavigationCommand()
}

data class NavigationOptions(
    var launchSingleTop: Boolean = false,
    var popUpToRoute: String? = null,
    var inclusive: Boolean = false
)
