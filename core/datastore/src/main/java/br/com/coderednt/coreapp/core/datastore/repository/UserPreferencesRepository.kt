package br.com.coderednt.coreapp.core.datastore.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import br.com.coderednt.coreapp.core.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação do [SettingsRepository] utilizando Jetpack DataStore.
 * 
 * Esta classe gerencia a persistência de preferências simples do usuário de forma 
 * assíncrona e segura em relação a threads.
 * 
 * @property dataStore Instância do DataStore injetada pelo Hilt.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) : SettingsRepository {

    /**
     * Chaves utilizadas para identificar os valores no DataStore.
     */
    private object PreferencesKeys {
        val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    /**
     * Obtém o modo de tema atual. 
     * Retorna "SYSTEM" como valor padrão caso nenhuma preferência tenha sido salva.
     */
    override fun getThemeMode(): Flow<String> = dataStore.data.map { preferences ->
        preferences[PreferencesKeys.THEME_MODE] ?: "SYSTEM"
    }

    /**
     * Persiste a nova escolha de tema do usuário no DataStore.
     * 
     * @param mode O identificador do tema.
     */
    override suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.THEME_MODE] = mode
        }
    }
}
