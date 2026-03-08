package br.com.coderednt.coreapp.core.monitoring.performance

interface ModuleInitializer {
    val name: String
    val isParallel: Boolean get() = false
    fun initialize()
}
