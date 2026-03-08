# Roadmap de Refatoração e Refinamento Arquitetural

Este roadmap visa melhorar a separação de responsabilidades, consistência técnica e escalabilidade do Core-Application.

## 1. Padronização de Métricas de Tempo
- [x] Substituir todos os usos de `System.nanoTime()` por `SystemClock.elapsedRealtimeNanos()` para garantir consistência entre as métricas de startup e execução.
- [x] Criar uma classe utilitária `TimeUtils` no `core:common` para conversão padronizada de Nanos para Milis.

## 2. Refatoração do AppHealthTracker (Separação de Responsabilidades)
- [x] **Desmembrar HealthMetrics**: Dividir o data class gigante em sub-modelos:
    - `StartupMetrics`: Focada no boot do app.
    - `UIMetrics`: Renderização, Jank e Navegação.
    - `SystemMetrics`: Memory e Bateria.
- [x] **Delegados de Monitoramento**: Criar interfaces específicas (`MemoryTracker`, `BatteryTracker`, `UITracker`) para que o `AppHealthTracker` apenas coordene e exponha o estado consolidado.
- [x] **Remover Reflexão**: Eliminar o fallback de `newInstance()` no carregamento de módulos, forçando a configuração correta via Hilt Multi-bindings.

## 3. Evolução do PerformanceMonitor
- [x] **Jank != Error**: Criar um canal específico para métricas de Jank no `HealthMetrics` em vez de usar `trackError`.
- [x] **ActivityTracker Automático**: Implementar um `ActivityLifecycleCallbacks` global para monitorar tempo de vida e memória das Activities sem exigir herança de `BaseActivity`.
- [x] **Métricas de Frame (Compose)**: Integrar monitoramento específico para recomposições do Jetpack Compose via `CompositionLocal` ou `CompositionTracing`.

## 4. Melhorias no core:common e core:monitoring
- [x] **Lifecycle-Aware Monitoring**: Fazer com que os trackers de bateria e memória respeitem o ciclo de vida do processo (parar monitoramento em background se não necessário).
- [x] **Safe Initializers**: Adicionar verificação de dependências circulares entre os `ModuleInitializer` e proteção contra re-inicialização.

## 5. Qualidade e Cobertura de Testes
- [x] **Core Performance**: Implementar testes unitários para o `AppHealthTrackerImpl` cobrindo lógica de métricas e proteção de inicialização.
- [x] **Core Common**: Testar utilitários de tempo (`TimeUtils`) e extensões base.
- [x] **Core Monitoring**: Validar contratos de interfaces e integridade dos modelos de dados.
- [x] **Feature Performance**: 
    - [x] Testar `ResourceMonitor` (Simulação de eventos de bateria e memória).
    - [x] Testar `PerformanceMonitorImpl` (Cálculos de TTID/TTFD).
- [ ] **UI & App (Meta: 80%)**:
    - [x] Validar estados do `PerformanceViewModel`.
    - [x] Testes de lógica de navegação.
    - [ ] Testes de UI/Compose para `PerformanceDashboardScreen`.
    - [ ] Testes de integração de fluxos na `MainScreen`.
- [x] **Integração**:
    - [x] Testes de integração do grafo Hilt (Hilt Testing).

## 6. Documentação
- [x] Adicionar KDoc explicativo sobre a hierarquia de módulos (Core vs Features).
- [x] Documentar o fluxo de inicialização segura para novos desenvolvedores.
