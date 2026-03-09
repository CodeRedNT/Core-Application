package br.com.coderednt.coreapp.core.common.util

import android.os.SystemClock

/**
 * Utilitários para medição e conversão de tempo no ecossistema Android.
 * 
 * Esta classe centraliza o uso de [SystemClock.elapsedRealtimeNanos] para garantir
 * consistência em todas as métricas de performance do SDK, evitando problemas
 * com mudanças no relógio do sistema (wall clock).
 */
object TimeUtils {

    /**
     * Retorna o tempo atual em nanossegundos desde o boot, incluindo o tempo gasto em deep sleep.
     * 
     * @return Tempo em nanossegundos.
     */
    fun nowNanos(): Long = SystemClock.elapsedRealtimeNanos()

    /**
     * Converte uma duração de nanossegundos para milissegundos com precisão decimal.
     * 
     * @param nanos O valor em nanossegundos a ser convertido.
     * @return O valor convertido em milissegundos (Double).
     */
    fun nanosToMillis(nanos: Long): Double = nanos / 1_000_000.0

    /**
     * Calcula a duração em milissegundos entre um ponto de tempo inicial e o momento atual.
     * 
     * @param startNanos O ponto inicial em nanossegundos (obtido via [nowNanos]).
     * @return A duração calculada em milissegundos. Retorna 0.0 se [startNanos] for inválido.
     */
    fun calculateDurationFrom(startNanos: Long): Double {
        if (startNanos <= 0) return 0.0
        val current = nowNanos()
        return nanosToMillis(current - startNanos)
    }
}
