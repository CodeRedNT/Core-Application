package br.com.coderednt.coreapp.core.common.performance

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Process
import android.os.SystemClock

object AppStartupTracker {
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
                providerStartTimeNanos - 100_000_000L // Fallback de 100ms
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

class AppStartupProvider : ContentProvider() {
    override fun onCreate(): Boolean {
        AppStartupTracker.init()
        return true
    }
    override fun query(u: Uri, p: Array<out String>?, s: String?, a: Array<out String>?, o: String?): Cursor? = null
    override fun getType(u: Uri): String? = null
    override fun insert(u: Uri, v: ContentValues?): Uri? = null
    override fun delete(u: Uri, s: String?, a: Array<out String>?): Int = 0
    override fun update(u: Uri, v: ContentValues?, s: String?, a: Array<out String>?): Int = 0
}
