package br.com.coderednt.coreapp.core.navigation

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação concreta do [Navigator] para uso com Jetpack Compose.
 * 
 * Esta classe utiliza um [MutableSharedFlow] para emitir comandos de navegação 
 * que são consumidos pelo NavHost na camada de UI.
 */
@Singleton
class ComposeNavigator @Inject constructor() : Navigator {
    
    private val _navigationEvents = MutableSharedFlow<NavigationCommand>(extraBufferCapacity = 1)
    
    /**
     * Fluxo compartilhado de eventos de navegação. 
     * O `extraBufferCapacity` garante que eventos não sejam perdidos se disparados 
     * em rápida sucessão antes do coletor estar pronto.
     */
    override val navigationEvents = _navigationEvents.asSharedFlow()

    /**
     * Dispara um comando de navegação para a rota especificada.
     */
    override fun navigate(route: String, builder: NavigationOptions.() -> Unit) {
        val options = NavigationOptions().apply(builder)
        _navigationEvents.tryEmit(NavigationCommand.Navigate(route, options))
    }

    /**
     * Dispara um comando para retornar na pilha de navegação.
     */
    override fun navigateUp() {
        _navigationEvents.tryEmit(NavigationCommand.NavigateUp)
    }
}
