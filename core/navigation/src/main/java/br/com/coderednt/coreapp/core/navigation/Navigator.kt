package br.com.coderednt.coreapp.core.navigation

import kotlinx.coroutines.flow.SharedFlow

/**
 * Interface para gerenciar a navegação de forma desacoplada e testável.
 * 
 * O Navigator permite que ViewModels disparem comandos de navegação sem 
 * depender diretamente do NavController do Compose, facilitando testes 
 * unitários e a modularização.
 */
interface Navigator {
    /**
     * Fluxo de eventos de navegação que devem ser observados pela UI (NavHost).
     */
    val navigationEvents: SharedFlow<NavigationCommand>
    
    /**
     * Solicita a navegação para uma rota específica.
     * 
     * @param route A rota de destino (string).
     * @param builder Configurações opcionais de navegação (launchSingleTop, popUpTo, etc).
     */
    fun navigate(
        route: String,
        builder: NavigationOptions.() -> Unit = {}
    )
    
    /**
     * Solicita o retorno para a tela anterior na pilha.
     */
    fun navigateUp()
}

/**
 * Representa os comandos de navegação possíveis no ecossistema.
 */
sealed class NavigationCommand {
    /**
     * Comando para navegar para uma nova rota.
     */
    data class Navigate(
        val route: String,
        val options: NavigationOptions = NavigationOptions()
    ) : NavigationCommand()
    
    /**
     * Comando para voltar na pilha de navegação.
     */
    object NavigateUp : NavigationCommand()
}

/**
 * Opções de configuração para uma transição de tela.
 * 
 * @property launchSingleTop Se verdadeiro, evita múltiplas instâncias da mesma tela no topo da pilha.
 * @property popUpToRoute Rota de destino para limpar a pilha até ela.
 * @property inclusive Se verdadeiro, a rota definida em [popUpToRoute] também será removida da pilha.
 */
data class NavigationOptions(
    var launchSingleTop: Boolean = false,
    var popUpToRoute: String? = null,
    var inclusive: Boolean = false
)
