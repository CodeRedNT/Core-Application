# 4. Performance e UX

O Core-Application prioriza a fluidez da interface e a transparência sobre a saúde do sistema.

## Navegação (Navigation Compose)
Utilizamos o **Jetpack Navigation Compose**. Para manter o desacoplamento:
*   ViewModels utilizam a interface `Navigator`.
*   Comandos são emitidos via `SharedFlow`.
*   O `NavHost` no módulo `:app` consome esses eventos.

## SplashScreen API
A **AndroidX SplashScreen API** é utilizada para gerenciar o estado de boot visual, garantindo que o app só apareça quando os recursos mínimos estiverem prontos.

## Metrics Performance
Utilizamos **AndroidX Metrics** para:
1.  **Jank Stats:** Detecção de frames que excedem o threshold de 16ms.
2.  **Startup Metrics:** Rastreamento do tempo desde o kernel até o TTID.

---
[⬅️ Voltar ao Índice](../../ARCHITECTURE_GUIDE.md) | [Anterior: Inicializadores](03-initializers.md) | [Próximo: Qualidade ➡️](05-quality.md)
