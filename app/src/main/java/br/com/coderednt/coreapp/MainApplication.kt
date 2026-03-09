package br.com.coderednt.coreapp

import br.com.coderednt.coreapp.core.analytics.performance.AnalyticsModuleInitializer
import br.com.coderednt.coreapp.core.architecture.BaseApplication
import br.com.coderednt.coreapp.core.common.performance.CommonModuleInitializer
import br.com.coderednt.coreapp.core.database.performance.DatabaseModuleInitializer
import br.com.coderednt.coreapp.core.datastore.performance.DataStoreModuleInitializer
import br.com.coderednt.coreapp.core.monitoring.performance.*
import br.com.coderednt.coreapp.core.navigation.performance.NavigationModuleInitializer
import br.com.coderednt.coreapp.core.ui.performance.UiModuleInitializer
import br.com.coderednt.coreapp.features.performance.performance.PerformanceModuleInitializer
import dagger.hilt.android.HiltAndroidApp

/**
 * Ponto de entrada principal do aplicativo Core-Application.
 * 
 * Esta classe estende [BaseApplication] para herdar as capacidades de 
 * monitoramento automático de startup e tratamento global de erros. 
 * Utiliza Hilt para injeção de dependências em todo o grafo do app.
 */
@HiltAndroidApp
class MainApplication : BaseApplication() {

    /**
     * Orquestra a inicialização dos módulos do SDK.
     * 
     * Utilizamos uma DSL de inicialização segura para garantir que os módulos 
     * críticos sejam carregados na ordem correta, medindo o impacto de cada um 
     * no tempo total de boot do aplicativo.
     */
    override fun onCreateModules() {
        appHealthTracker.sync {
            // --- INFRAESTRUTURA BASE (Essencial para o funcionamento do SDK) ---
            module<MonitoringModuleInitializer>()
            module<CommonModuleInitializer>()
            
            // --- CORE SERVICES (Persistência, Analytics e Navegação) ---
            module<DatabaseModuleInitializer>()
            module<DataStoreModuleInitializer>()
            module<AnalyticsModuleInitializer>()
            module<NavigationModuleInitializer>()
            
            // --- UI & FEATURES (Componentes visuais e monitoramento ativo) ---
            module<UiModuleInitializer>()
            module<PerformanceModuleInitializer>()
        }

        appHealthTracker.async {
            // Reservado para inicializações pesadas que não impedem a exibição da UI inicial.
            // Exemplo: Pré-carregamento de cache, logs de auditoria não críticos, etc.
        }
    }
}
