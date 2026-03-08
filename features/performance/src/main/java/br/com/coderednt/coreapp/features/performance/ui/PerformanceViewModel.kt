package br.com.coderednt.coreapp.features.performance.ui

import androidx.lifecycle.ViewModel
import br.com.coderednt.coreapp.core.common.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.common.performance.HealthMetrics
import br.com.coderednt.coreapp.features.performance.performance.PerformanceModuleInitializer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) : ViewModel() {

    val uiState: StateFlow<HealthMetrics> = appHealthTracker.metrics

    init {
        // Agora carrega a feature passando a classe (o tracker cuida de buscar a instância se necessário)
        // ou se já foi carregada no startup, não fará nada (pode ser otimizado no futuro)
        appHealthTracker.load(PerformanceModuleInitializer::class.java, isParallel = true)
    }
}
