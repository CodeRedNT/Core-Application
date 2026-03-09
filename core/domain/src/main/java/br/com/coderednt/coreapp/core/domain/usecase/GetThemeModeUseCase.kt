package br.com.coderednt.coreapp.core.domain.usecase

import br.com.coderednt.coreapp.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * UseCase para recuperar o modo de tema atual do usuário.
 */
class GetThemeModeUseCase @Inject constructor(
    private val repository: SettingsRepository
) {
    operator fun invoke(): Flow<String> = repository.getThemeMode()
}
