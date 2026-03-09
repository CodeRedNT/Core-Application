package br.com.coderednt.coreapp.core.analytics

/**
 * Interface para abstração de ferramentas de Analytics.
 * 
 * Esta interface permite que o aplicativo envie eventos para diferentes provedores 
 * (Firebase, Mixpanel, Logcat, etc.) sem se acoplar a uma implementação específica.
 */
interface AnalyticsHelper {
    /**
     * Registra um evento de analytics.
     * 
     * @param event O objeto [AnalyticsEvent] contendo o tipo e os parâmetros.
     */
    fun logEvent(event: AnalyticsEvent)
}

/**
 * Representação de um evento de Analytics.
 * 
 * @property type O nome ou categoria do evento (ex: "button_click", "screen_view").
 * @property extras Lista de parâmetros adicionais para detalhar o evento.
 */
data class AnalyticsEvent(
    val type: String,
    val extras: List<AnalyticsEventExtra> = emptyList()
)

/**
 * Parâmetro chave-valor para um evento de Analytics.
 * 
 * @property key O nome do parâmetro.
 * @property value O valor associado ao parâmetro.
 */
data class AnalyticsEventExtra(
    val key: String,
    val value: String
)
