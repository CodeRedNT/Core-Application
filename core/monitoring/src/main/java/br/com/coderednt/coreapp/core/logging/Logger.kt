package br.com.coderednt.coreapp.core.logging

/**
 * Interface para logging estruturado no SDK.
 */
interface Logger {
    fun d(message: String, vararg args: Any?)
    fun i(message: String, vararg args: Any?)
    fun w(message: String, vararg args: Any?)

    /** 
     * Loga um erro e o reporta para o AppHealthTracker.
     * Este é o método preferencial para capturar exceções na UI e na camada de Domínio.
     */
    fun logAndTrack(t: Throwable? = null, message: String, vararg args: Any?)

    /**
     * Loga um erro SOMENTE no motor de log, sem reportar ao AppHealthTracker.
     * Utilizado internamente pelo SDK para evitar loops de dependência.
     */
    fun e(t: Throwable? = null, message: String, vararg args: Any?)
}
