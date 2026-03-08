# ==============================================================================
# REGRAS ROBUSTAS DE PROGUARD/R8 - PERFORMANCE E SEGURANÇA
# ==============================================================================

# ------------------------------------------------------------------------------
# 1. OTIMIZAÇÃO E OFUSCAÇÃO GERAL
# ------------------------------------------------------------------------------

# Ativa otimizações agressivas (já incluído pelo proguard-android-optimize.txt, mas reforçado aqui)
-allowaccessmodification
-mergeinterfacesaggressively

# Mantém informações de depuração úteis para Crashlytics (mantendo segurança)
-keepattributes SourceFile,LineNumberTable,*Annotation*

# Ofuscação de nomes de arquivos fonte
-renamesourcefileattribute SourceFile

# ------------------------------------------------------------------------------
# 2. JETPACK COMPOSE & KOTLIN
# ------------------------------------------------------------------------------

# Compose depende de metadados e reflexão interna em alguns pontos
-keep class androidx.compose.runtime.** { *; }
-keep @androidx.compose.runtime.Composable class * { *; }

# Kotlin Coroutines e Reflection
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.coroutines.android.HandlerContext {
    java.lang.String name;
}

# ------------------------------------------------------------------------------
# 3. HILT & DAGGER (CRÍTICO)
# ------------------------------------------------------------------------------

# Mantém as anotações do Hilt e as classes geradas
-keep class * extends android.app.Application
-keep class * extends android.app.Activity
-keep class * extends androidx.lifecycle.ViewModel
-keep @dagger.hilt.android.lifecycle.HiltViewModel class * { *; }

# Mantém os entry points e módulos (evita que o R8 remova por "não uso" aparente)
-keep @dagger.Module class * { *; }
-keep @dagger.hilt.InstallIn class * { *; }

# ------------------------------------------------------------------------------
# 4. MONITORAMENTO E INITIALIZERS (MANTÉM NOSSA DSL DE STARTUP)
# ------------------------------------------------------------------------------

# Mantém nossa interface e anotação customizada para Multibinding
-keep interface br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer { *; }
-keep class * implements br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer { *; }
-keep @br.com.coderednt.coreapp.core.monitoring.performance.StartupKey class * { *; }

# Mantém os nomes das classes que usamos como chaves no Mapa do Hilt
-keepnames class * implements br.com.coderednt.coreapp.core.monitoring.performance.ModuleInitializer

# ------------------------------------------------------------------------------
# 5. SEGURANÇA E PROTEÇÃO DE DADOS
# ------------------------------------------------------------------------------

# Remove logs de produção para performance e segurança (exceto erros críticos se desejado)
-assumenosideeffects class android.util.Log {
    public static int v(...);
    public static int d(...);
    public static int i(...);
    public static int w(...);
}

# Protege modelos de dados que podem ser serializados (GSON/Jackson/Kotlin Serialization)
# Se usar alguma lib de serialização, adicione os @Keep nos modelos ou as regras aqui:
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# ------------------------------------------------------------------------------
# 6. BIBLIOTECAS ESPECÍFICAS
# ------------------------------------------------------------------------------

# JankStats (Android X Metrics)
-keep class androidx.metrics.performance.** { *; }
