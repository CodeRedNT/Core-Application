package br.com.coderednt.coreapp.core.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Interface de repositório para gerenciamento de configurações globais do usuário.
 * 
 * Esta interface é definida na camada de domínio para seguir o princípio de 
 * inversão de dependência (DIP), permitindo que o domínio não conheça detalhes 
 * de implementação como DataStore ou SharedPreferences.
 */
interface SettingsRepository {
    
    /**
     * Recupera o modo de tema atual salvo pelo usuário.
     * 
     * @return Um [Flow] emitindo o identificador do tema (ex: "light", "dark", "system").
     */
    fun getThemeMode(): Flow<String>

    /**
     * Atualiza o modo de tema preferido do usuário.
     * 
     * @param mode O novo identificador do tema a ser persistido.
     */
    suspend fun setThemeMode(mode: String)
}
