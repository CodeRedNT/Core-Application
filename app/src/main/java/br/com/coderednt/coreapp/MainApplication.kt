package br.com.coderednt.coreapp

import br.com.coderednt.coreapp.core.common.base.BaseApplication
import br.com.coderednt.coreapp.core.monitoring.performance.*
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

/**
 * Classe de aplicação principal que inicializa o grafo de dependências do Hilt
 * e configura os módulos do sistema através do AppHealthTracker.
 * Atualizada para carregar módulos automaticamente via injeção de mapa.
 */
@HiltAndroidApp
class MainApplication : BaseApplication() {

    @Inject
    lateinit var initializers: Map<Class<out ModuleInitializer>, @JvmSuppressWildcards ModuleInitializer>

    /**
     * Define a ordem de inicialização dos módulos do aplicativo.
     * Carrega todos os módulos vinculados ao Hilt automaticamente.
     */
    override fun onCreateModules() {
        appHealthTracker.sync {
            // Carrega dinamicamente todos os inicializadores registrados no Hilt
            initializers.values.forEach { initializer ->
                appHealthTracker.loadModule(initializer)
            }
        }

        appHealthTracker.async {
            // Reservado para futuros módulos assíncronos
        }
    }
}
