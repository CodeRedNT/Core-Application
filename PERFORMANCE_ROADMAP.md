# Roadmap de Implementação: Performance Dashboard Vitals

Este documento descreve as etapas para implementação das novas métricas de performance e a reestruturação do Dashboard.

## 1. Infraestrutura de Dados (Core)
- [x] Atualizar `HealthMetrics` para incluir:
    - [x] Métricas de Memória (Total, Usada, Disponível).
    - [x] Métricas de Bateria (Nível, Status de Carregamento, Temperatura).
- [x] Atualizar `AppHealthTracker` com métodos de registro:
    - [x] `trackMemory(metrics: MemoryMetrics)`
    - [x] `trackBattery(metrics: BatteryMetrics)`

## 2. Implementação de Tracking (Internal)
- [x] Criar `ResourceMonitor` para coleta de Memória e Bateria.
- [x] Integrar novos trackers no `AppHealthTrackerImpl`.
- [x] Garantir inicialização imediata e tratamento de erros no loop.

## 3. Interface do Usuário (UI/Dashboard)
- [x] Implementar `TabRow` para seleção de Vitals:
    - [x] **Startup**: TTID, TTFD e Fases.
    - [x] **Network**: Latências de API.
    - [x] **Memory**: Uso de heap e memória do sistema.
    - [x] **Battery**: Status de consumo e temperatura.
- [x] Implementar navegação entre categorias via Switch/Tabs.

## 4. Polimento e Documentação
- [x] Documentar funções principais em KDoc.
- [ ] Validar build final e ausência de vazamentos de memória no tracker.
