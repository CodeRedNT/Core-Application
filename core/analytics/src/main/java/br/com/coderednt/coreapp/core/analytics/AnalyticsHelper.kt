package br.com.coderednt.coreapp.core.analytics

/**
 * Interface para abstração de ferramentas de Analytics.
 */
interface AnalyticsHelper {
    fun logEvent(event: AnalyticsEvent)
}

/**
 * Representação de um evento de Analytics.
 */
data class AnalyticsEvent(
    val type: String,
    val extras: List<AnalyticsEventExtra> = emptyList()
)

data class AnalyticsEventExtra(
    val key: String,
    val value: String
)
