package br.com.coderednt.coreapp.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface de repositório para configurações de usuário.
 * Definida no domínio para ser implementada na camada de dados (DataStore).
 */
interface SettingsRepository {
    fun getThemeMode(): Flow<String>
    suspend fun setThemeMode(mode: String)
}
