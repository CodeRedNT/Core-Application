package br.com.coderednt.coreapp.core.common.performance

interface ModuleInitializer {
    val name: String
    val isParallel: Boolean get() = false
    fun initialize()
}
