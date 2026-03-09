package br.com.coderednt.coreapp.core.monitoring.performance

import dagger.MapKey
import kotlin.reflect.KClass

@MapKey
annotation class StartupKey(val value: KClass<out ModuleInitializer>)
