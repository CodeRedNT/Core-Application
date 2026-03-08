package br.com.coderednt.coreapp.features.performance.internal

import android.os.Build
import android.os.Process
import android.os.SystemClock

/**
 * Objeto interno ao módulo de performance para gerenciar os tempos de boot.
 * Totalmente isolado do core:common.
 */
internal object AppStartupTracker {
    var processStartTimeNanos: Long = 0
        private set
    var providerStartTimeNanos: Long = 0
        private set
    var appStartTimeNanos: Long = 0
        private set
    var appEndTimeNanos: Long = 0
        private set

    var isTtidReported: Boolean = false

    fun init() {
        if (providerStartTimeNanos == 0L) {
            providerStartTimeNanos = SystemClock.elapsedRealtimeNanos()
            
            val kernelStartMs = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                Process.getStartElapsedRealtime()
            } else 0L
            
            processStartTimeNanos = if (kernelStartMs > 0) {
                kernelStartMs * 1_000_000L
            } else {
                providerStartTimeNanos - 100_000_000L // Fallback
            }
        }
    }
    
    fun markAppStart() {
        if (appStartTimeNanos == 0L) {
            appStartTimeNanos = SystemClock.elapsedRealtimeNanos()
        }
    }

    fun markAppEnd() {
        if (appEndTimeNanos == 0L) {
            appEndTimeNanos = SystemClock.elapsedRealtimeNanos()
        }
    }
}
