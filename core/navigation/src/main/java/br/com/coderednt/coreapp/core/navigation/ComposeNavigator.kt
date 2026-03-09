package br.com.coderednt.coreapp.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ComposeNavigator @Inject constructor() : Navigator {
    private val _navigationEvents = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 1)
    override val navigationEvents = _navigationEvents.asSharedFlow()

    override fun navigate(route: String, builder: NavigationOptions.() -> Unit) {
        val options = NavigationOptions().apply(builder)
        _navigationEvents.tryEmit(NavigationCommand.Navigate(route, options))
    }

    override fun navigateUp() {
        _navigationEvents.tryEmit(NavigationCommand.NavigateUp)
    }
}
