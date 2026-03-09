package br.com.coderednt.coreapp.features.performance.internal

import android.os.Build
import android.os.Process
import br.com.coderednt.coreapp.core.common.util.TimeUtils

/**
 * Objeto interno responsável por capturar e armazenar os carimbos de data/hora (timestamps) 
 * críticos durante o processo de inicialização do aplicativo.
 * 
 * Ele rastreia desde o início do processo no kernel até o final do onCreate da Application, 
 * permitindo o cálculo do overhead do SO e tempos de inicialização de providers.
 */
internal object AppStartupTracker {
    
    /** Tempo em que o processo foi iniciado pelo sistema operacional. */
    var processStartTimeNanos: Long = 0
        private set
        
    /** Tempo em que o primeiro ContentProvider do SDK foi inicializado. */
    var providerStartTimeNanos: Long = 0
        private set
        
    /** Tempo de início do método onCreate da Application. */
    var appStartTimeNanos: Long = 0
        private set
        
    /** Tempo de término do método onCreate da Application. */
    var appEndTimeNanos: Long = 0
        private set

    /** Indica se o Time To Initial Display (TTID) já foi reportado para evitar duplicidade. */
    var isTtidReported: Boolean = false

    /**
     * Inicializa os carimbos de tempo iniciais. 
     * Tenta obter o tempo real de início do processo via API do Android (API 30+) 
     * ou utiliza um fallback para versões anteriores.
     */
    fun init() {
        if (providerStartTimeNanos == 0L) {
            providerStartTimeNanos = TimeUtils.nowNanos()
            
            val kernelStartMs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Process.getStartElapsedRealtime()
            } else 0L
            
            processStartTimeNanos = if (kernelStartMs > 0) {
                kernelStartMs * 1_000_000L
            } else {
                // Caso não seja possível obter do kernel, estima um overhead de 100ms
                providerStartTimeNanos - 100_000_000L 
            }
        }
    }
    
    /**
     * Registra o início da execução da classe Application.
     */
    fun markAppStart() {
        if (appStartTimeNanos == 0L) {
            appStartTimeNanos = TimeUtils.nowNanos()
        }
    }

    /**
     * Registra o término da execução da classe Application.
     */
    fun markAppEnd() {
        if (appEndTimeNanos == 0L) {
            appEndTimeNanos = TimeUtils.nowNanos()
        }
    }
}
