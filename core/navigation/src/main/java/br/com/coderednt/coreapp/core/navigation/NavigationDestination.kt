package br.com.coderednt.coreapp.core.navigation

import androidx.navigation.NamedNavArgument

/**
 * Interface base para todas as rotas do aplicativo.
 */
interface NavigationDestination {
    val route: String
    val arguments: List<NamedNavArgument>
        get() = emptyList()
}

/**
 * Destinos padrão do Core Application.
 */
object CoreDestinations {
    object Performance : NavigationDestination {
        override val route = "performance"
    }
}
