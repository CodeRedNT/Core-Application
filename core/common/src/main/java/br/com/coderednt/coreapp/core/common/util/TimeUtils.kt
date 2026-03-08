package br.com.coderednt.coreapp.core.common.util

import android.os.SystemClock

/**
 * Utilitários para medição e conversão de tempo no ecossistema Android.
 * Centraliza o uso de SystemClock.elapsedRealtimeNanos() para consistência.
 */
object TimeUtils {

    /**
     * Retorna o tempo atual em nanossegundos desde o boot.
     * Mais preciso e consistente que System.nanoTime() para métricas de performance.
     */
    fun nowNanos(): Long = SystemClock.elapsedRealtimeNanos()

    /**
     * Converte nanossegundos para milissegundos com precisão decimal.
     */
    fun nanosToMillis(nanos: Long): Double = nanos / 1_000_000.0

    /**
     * Calcula a duração entre um ponto de início (nanos) e o agora.
     */
    fun calculateDurationFrom(startNanos: Long): Double {
        if (startNanos <= 0) return 0.0
        return nanosToMillis(nowNanos() - startNanos)
    }
}
