# 1. Princípios de Design

Este documento detalha os princípios que regem o desenvolvimento do Core-Application.

*   **Separação de Preocupações (SoC):** Cada módulo tem uma responsabilidade única e bem definida.
*   **Padrão Unidirectional Data Flow (UDF):** A UI dispara eventos e observa estados. O estado é imutável e centralizado no ViewModel.
*   **Independência de Framework:** O módulo `:domain` é puramente Kotlin, garantindo que as regras de negócio sejam testáveis sem dependências do Android.
*   **Injeção de Dependência:** Hilt é utilizado para gerenciar o ciclo de vida e a provisão de dependências em todo o grafo.

---
[⬅️ Voltar ao Índice](../../ARCHITECTURE_GUIDE.md) | [Próximo: Estrutura de Camadas ➡️](02-layers.md)
