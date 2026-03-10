package br.com.coderednt.coreapp.core.domain.manager

/**
 * Interface responsável por detectar e reportar informações sobre o ambiente de execução.
 * 
 * Permite identificar se o aplicativo está rodando em um emulador ou em um dispositivo 
 * com acesso root, o que é crítico para validar a integridade das métricas de performance 
 * e segurança.
 */
interface EnvironmentManager {
    /**
     * Verifica se o ambiente de execução é um emulador.
     */
    fun isEmulator(): Boolean

    /**
     * Verifica se o dispositivo possui acesso root.
     */
    fun isRooted(): Boolean

    /**
     * Retorna uma string descritiva do ambiente (ex: "Emulator", "Rooted Device", "Secure Device").
     */
    fun getEnvironmentType(): String
}
