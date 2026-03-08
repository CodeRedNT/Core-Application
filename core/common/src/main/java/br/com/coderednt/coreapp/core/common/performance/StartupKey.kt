package br.com.coderednt.coreapp.core.common.performance

import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Anotação customizada para registrar módulos de startup.
 * Abstrai as complexidades de Multibindings do Hilt/Dagger.
 */
@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION)
annotation class StartupKey(val value: KClass<out ModuleInitializer>)
