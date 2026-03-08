package br.com.coderednt.coreapp.features.performance.ui

import androidx.lifecycle.ViewModel
import br.com.coderednt.coreapp.core.common.performance.AppHealthTracker
import br.com.coderednt.coreapp.core.common.performance.HealthMetrics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PerformanceViewModel @Inject constructor(
    private val appHealthTracker: AppHealthTracker
) : ViewModel() {

    val uiState: StateFlow<HealthMetrics> = appHealthTracker.metrics

    init {
        // O carregamento do módulo Performance já deve ter sido feito na Application.
        // Removi a chamada ao método load() que foi removido da interface AppHealthTracker.
    }
}
