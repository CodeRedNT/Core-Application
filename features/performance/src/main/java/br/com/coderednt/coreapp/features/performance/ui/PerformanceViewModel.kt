package br.com.coderednt.coreapp.features.performance.ui

import br.com.coderednt.coreapp.core.architecture.BaseViewModel
import br.com.coderednt.coreapp.core.monitoring.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.monitoring.performance.HealthMetrics
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Eventos disparados pela tela de Dashboard de Performance.
 */
sealed class PerformanceEvent {
    object Refresh : PerformanceEvent()
}

/**
 * ViewModel responsável por prover o estado das métricas de saúde do app 
 * para a UI de monitoramento.
 * 
 * Estende [BaseViewModel] para seguir o padrão UDF do SDK.
 * 
 * @property appHealthTracker Fonte de dados em tempo real das métricas do sistema.
 */
@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) : BaseViewModel<HealthMetrics, PerformanceEvent>(appHealthTracker.metrics.value) {

    /**
     * Observa as mudanças no tracker e atualiza o estado interno do ViewModel.
     * Nota: Como o tracker já expõe um StateFlow, poderíamos mapeá-lo diretamente, 
     * mas aqui seguimos a estrutura de BaseViewModel para consistência.
     */
    val metricsState = appHealthTracker.metrics

    override fun onEvent(event: PerformanceEvent) {
        when (event) {
            is PerformanceEvent.Refresh -> {
                // Lógica de atualização manual se necessário
            }
        }
    }
}
