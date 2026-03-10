package br.com.coderednt.coreapp.core.domain.repository

/**
 * Interface para armazenamento seguro de dados sensíveis.
 * 
 * Define o contrato para persistência de informações que devem ser 
 * criptografadas em repouso (at-rest), como tokens de autenticação 
 * ou chaves de API.
 */
interface SecureStorageRepository {
    /**
     * Salva uma string de forma segura.
     */
    suspend fun saveString(key: String, value: String)

    /**
     * Recupera uma string salva de forma segura.
     * 
     * @return O valor descriptografado ou null se não existir.
     */
    suspend fun getString(key: String): String?

    /**
     * Remove um valor específico do armazenamento.
     */
    suspend fun delete(key: String)

    /**
     * Limpa todo o armazenamento seguro.
     */
    suspend fun clear()
}
