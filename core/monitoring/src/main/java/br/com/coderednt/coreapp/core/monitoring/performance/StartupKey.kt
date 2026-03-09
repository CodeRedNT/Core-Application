package br.com.coderednt.coreapp.core.monitoring.performance

import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Anotação MapKey utilizada pelo Hilt/Dagger para identificar inicializadores de módulos.
 * 
 * Permite a injeção de um mapa de [ModuleInitializer] onde a chave é a classe do inicializador.
 * 
 * @property value A classe do inicializador que será usada como chave no Multibinding.
 */
@MapKey
annotation class StartupKey(val value: KClass<out ModuleInitializer>)
