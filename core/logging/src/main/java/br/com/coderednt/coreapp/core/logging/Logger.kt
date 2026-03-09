package br.com.coderednt.coreapp.core.logging

import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import dagger.Lazy
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

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
     * Loga um erro SOMENTE no motor de log (Timber), sem reportar ao AppHealthTracker.
     * Utilizado internamente pelo SDK para evitar loops de dependência.
     */
    fun e(t: Throwable? = null, message: String, vararg args: Any?)
}

@Singleton
class LoggerImpl @Inject constructor(
    private val healthTracker: Lazy<AppHealthTracker>
) : Logger {

    override fun d(message: String, vararg args: Any?) {
        Timber.d(message, *args)
    }

    override fun i(message: String, vararg args: Any?) {
        Timber.i(message, *args)
    }

    override fun w(message: String, vararg args: Any?) {
        Timber.w(message, *args)
    }

    override fun logAndTrack(t: Throwable?, message: String, vararg args: Any?) {
        e(t, message, *args) // Loga primeiro
        healthTracker.get().trackError(String.format(message, *args))
    }

    override fun e(t: Throwable?, message: String, vararg args: Any?) {
        Timber.e(t, message, *args)
    }
}
