package br.com.coderednt.coreapp.core.datastore.security

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import br.com.coderednt.coreapp.core.domain.repository.SecureStorageRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação de [SecureStorageRepository] utilizando Jetpack Security.
 * 
 * Utiliza [EncryptedSharedPreferences] para garantir que os dados sejam 
 * criptografados em repouso utilizando chaves gerenciadas pelo Android Keystore.
 */
@Singleton
class SecureStorageRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SecureStorageRepository {

    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val sharedPreferences: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "secure_core_prefs",
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    override suspend fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    override suspend fun getString(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    override suspend fun delete(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }

    override suspend fun clear() {
        sharedPreferences.edit().clear().apply()
    }
}
