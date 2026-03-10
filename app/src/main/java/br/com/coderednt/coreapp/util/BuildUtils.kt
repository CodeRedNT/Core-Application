package br.com.coderednt.coreapp.util

import br.com.coderednt.BuildConfig

/**
 * Utilitário para facilitar o acesso a flags de build de forma segura e centralizada.
 */
object BuildUtils {
    /**
     * Indica se o aplicativo está rodando em modo de depuração (Debug).
     */
    val isDebug: Boolean get() = BuildConfig.DEBUG
}
