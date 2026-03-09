package br.com.coderednt.coreapp.core.architecture

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Classe base para ViewModels seguindo o padrão Unidirectional Data Flow (UDF).
 *
 * @param State O tipo que representa o estado da UI nesta tela.
 * @param Event O tipo que representa eventos disparados pela UI (intenções).
 * @property initialState O estado inicial da tela.
 */
abstract class BaseViewModel<State, Event>(initialState: State) : ViewModel() {

    private val _uiState = MutableStateFlow(initialState)

    /**
     * Fluxo de estado da UI observável pela View.
     */
    val uiState: StateFlow<State> = _uiState.asStateFlow()

    /**
     * Retorna o estado atual da UI de forma síncrona.
     */
    protected val currentState: State
        get() = _uiState.value

    /**
     * Função abstrata para processar eventos enviados pela View.
     *
     * @param event O evento a ser processado.
     */
    abstract fun onEvent(event: Event)

    /**
     * Atualiza o estado da UI de forma atômica.
     *
     * @param reducer Lambda que recebe o estado atual e retorna o novo estado.
     */
    protected fun updateState(reducer: State.() -> State) {
        _uiState.update { it.reducer() }
    }
}
