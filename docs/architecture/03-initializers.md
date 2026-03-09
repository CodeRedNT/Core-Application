# 3. Fluxo de Inicialização Segura (Safe Initializers)

O SDK utiliza um sistema de `ModuleInitializer` para evitar ANRs (Application Not Responding) durante o boot, permitindo que cada módulo gerencie sua própria inicialização de forma monitorada.

## Como funciona?
1.  **Interface `ModuleInitializer`**: Cada módulo define seu próprio inicializador.
2.  **Injeção via Hilt**: Os inicializadores são vinculados ao grafo de dependências usando Multibindings.
3.  **DSL de Inicialização**: Na classe `MainApplication`, utilizamos os métodos `sync` ou `async`.

### Exemplo de Registro:
```kotlin
@Binds
@IntoMap
@StartupKey(MyModuleInitializer::class)
abstract fun bindMyModule(impl: MyModuleInitializer): ModuleInitializer
```

### Exemplo de Uso:
```kotlin
appHealthTracker.sync {
    module<DatabaseModuleInitializer>()
}
```

---
[⬅️ Voltar ao Índice](../../ARCHITECTURE_GUIDE.md) | [Anterior: Camadas](02-layers.md) | [Próximo: Performance ➡️](04-performance.md)
