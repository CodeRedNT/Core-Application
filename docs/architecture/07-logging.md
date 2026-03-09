# 7. Logging Estruturado e Telemetria

O sistema de logging do Core-Application foi projetado para ser a base da observabilidade remota, permitindo que eventos e erros sejam capturados de forma estruturada.

## Módulo `:core:logging`
Este módulo fornece uma abstração pura para o sistema de log do SDK, encapsulando dependências externas (como o Timber) e integrando os logs diretamente com o monitoramento de saúde.

### Principais Funcionalidades:
*   **Abstração Total**: Nenhum módulo de feature (ex: `:performance`) deve depender diretamente de bibliotecas de log de terceiros. Toda interação deve ser feita via interface `Logger`.
*   **Prevenção de ANR**: Utiliza injeção preguiçosa (`Lazy<AppHealthTracker>`) para quebrar ciclos de dependência entre o motor de log e o rastreador de saúde.
*   **Segregação de Comportamento**:
    *   `logAndTrack()`: Registra o erro localmente e o envia para o Dashboard de Performance.
    *   `e()`: Registra o erro apenas localmente (Logcat), prevenindo recursão infinita em erros internos do SDK.

### Exemplo de Uso (Camada de App/Feature):
```kotlin
@Inject lateinit var logger: Logger

fun loadData() {
    try {
        // ... lógica de negócio
        logger.d("Dados carregados com sucesso")
    } catch (e: Exception) {
        // Registra o erro no Logcat e mostra no Dashboard de Performance
        logger.logAndTrack(e, "Erro ao processar dados do usuário")
    }
}
```

---
[⬅️ Voltar ao Índice](../../ARCHITECTURE_GUIDE.md) | [Anterior: Documentação](06-documentation.md)
