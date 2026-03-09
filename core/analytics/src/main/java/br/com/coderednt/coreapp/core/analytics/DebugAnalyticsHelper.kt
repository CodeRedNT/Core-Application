package br.com.coderednt.coreapp.core.analytics

import android.util.Log
import javax.inject.Inject

/**
 * Implementação de Analytics para ambiente de desenvolvimento.
 */
class DebugAnalyticsHelper @Inject constructor() : AnalyticsHelper {
    override fun logEvent(event: AnalyticsEvent) {
        val extras = event.extras.joinToString { "${it.key}=${it.value}" }
        Log.d("Analytics", "Event: ${event.type} | Extras: $extras")
    }
}
