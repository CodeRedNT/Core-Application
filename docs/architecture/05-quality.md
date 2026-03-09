# 5. Qualidade e Testes

Garantimos a confiabilidade do SDK através de análise estática e pirâmide de testes automatizados.

## Análise Estática (Detekt)
O **Detekt** valida a complexidade e o estilo do código Kotlin.
*   **Comando**: `./gradlew detektAll`
*   **Configuração**: `config/detekt/detekt.yml`

## Testes Unitários (Robolectric)
Utilizamos **Robolectric** para rodar testes que dependem do Android (ex: ViewModels, Context) na JVM. Isso nos dá velocidade de execução com fidelidade ao framework.

## Testes de Snapshot (Roborazzi)
O **Roborazzi** captura a representação visual dos componentes do módulo `:core:ui`.
*   **Gravar**: `./gradlew recordRoborazziDebug`
*   **Verificar**: `./gradlew verifyRoborazziDebug`

```kotlin
@Test
fun snapshot() {
    composeTestRule.setContent { CoreButton("Test") }
    composeTestRule.onRoot().captureRoboImage("button.png")
}
```

---
[⬅️ Voltar ao Índice](../../ARCHITECTURE_GUIDE.md) | [Anterior: Performance](04-performance.md) | [Próximo: Documentação ➡️](06-documentation.md)
