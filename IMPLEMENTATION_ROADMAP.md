# Roadmap de Implementação - CoreApp Performance & Health

Este documento norteia as próximas melhorias no sistema de monitoramento do CoreApp.

## 1. Interceptação de Erros Global (App Health)
- **Objetivo:** Capturar crashes não tratados para reportar o estado de "saúde" do app.
- **Implementação:** Adicionar `Thread.UncaughtExceptionHandler` na `BaseApplication`.
- **Status:** ✅ Concluído

## 2. Integração com Splash Screen (API 31+)
- **Objetivo:** Medir o tempo que a Splash Screen permanece visível.
- **Implementação:** Utilizar `setOnExitAnimationListener` na `MainActivity` e reportar a duração.
- **Status:** ✅ Concluído

## 3. Rastreamento de Latência de Navegação
- **Objetivo:** Medir o tempo de transição entre telas no Compose.
- **Implementação:** Interceptar mudanças de rota no `NavController` e medir o tempo no `CoreApp.kt`.
- **Status:** ✅ Concluído

## 4. Detecção de Jank (Frames perdidos)
- **Objetivo:** Identificar quedas de frame rate (stutter) durante o uso do app.
- **Implementação:** Integrar `JankStats` na `BaseActivity`.
- **Status:** ✅ Concluído
