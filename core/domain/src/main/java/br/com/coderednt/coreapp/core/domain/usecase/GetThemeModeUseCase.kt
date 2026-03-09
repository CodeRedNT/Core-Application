package br.com.coderednt.coreapp.core.domain.usecase

import br.com.coderednt.coreapp.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de Uso para recuperar a preferência de tema do usuário.
 * 
 * Este UseCase encapsula a lógica de negócio necessária para obter o modo de tema 
 * atual (claro, escuro ou sistema) através do [SettingsRepository].
 * 
 * Exemplo de uso:
 * ```
 * getThemeModeUseCase().collect { theme -> 
 *    // Atualizar UI com o novo tema
 * }
 * ```
 */
class GetThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    /**
     * Executa o caso de uso.
     * 
     * @return Um [Flow] contendo a String que representa o modo de tema.
     */
    operator fun invoke(): Flow<String> = repository.getThemeMode()
}
