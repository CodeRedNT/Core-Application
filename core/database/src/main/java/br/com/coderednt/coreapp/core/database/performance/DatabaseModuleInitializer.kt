package br.com.coderednt.coreapp.core.database.performance

import br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Inicializador para o módulo de banco de dados.
 * 
 * Embora o Room realize a inicialização de forma preguiçosa (lazy), este inicializador
 * permite registrar a presença do módulo no ciclo de vida de startup do SDK.
 */
@Singleton
class DatabaseModuleInitializer @Inject constructor() : ModuleInitializer {
    override val name: String = "database"
    
    override fun initialize() {
        // Log de inicialização ou verificações de integridade podem ser adicionados aqui.
    }
}
