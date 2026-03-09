# 2. Estrutura de Camadas

O Core-Application utiliza uma arquitetura modularizada para garantir escalabilidade e separação de responsabilidades.

## Camada de Arquitetura (`:core:architecture`)
Define os contratos base e infraestrutura comum.
*   **BaseViewModel:** Abstração para gerenciamento de estado (UDF).
*   **BaseActivity:** Integração nativa com monitoramento de performance.
*   **BaseApplication:** Orquestração de startup e tratamento de erros.

```kotlin
// Exemplo de ViewModel
class MyViewModel @Inject constructor() : BaseViewModel<MyState, MyEvent>(initialState) {
    override fun onEvent(event: MyEvent) { /* ... */ }
}
```

## Camada de Domínio (`:core:domain`)
Contém a lógica de negócio pura (Kotlin-only).
*   **UseCases:** Encapsulam uma única ação de negócio.
*   **Repositories (Interfaces):** Definem contratos de dados.

## Outras Camadas Core
*   `:core:database` / `:core:datastore`: Persistência.
*   `:core:ui`: Design System e componentes comuns.
*   `:core:navigation`: Infraestrutura de rotas.

---
[⬅️ Voltar ao Índice](../../ARCHITECTURE_GUIDE.md) | [Anterior: Princípios](01-principles.md) | [Próximo: Inicializadores ➡️](03-initializers.md)
