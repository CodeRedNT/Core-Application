# Guia de Arquitetura - SDK de Monitoramento

Este documento descreve a arquitetura do sistema de monitoramento de saúde e performance do aplicativo.

## 1. Estrutura de Módulos
O sistema é dividido em três camadas principais:
- **core:monitoring**: Define os contratos (interfaces) e os modelos de dados. Não possui lógica pesada.
- **core:common**: Fornece utilitários base como o `TimeUtils` para garantir consistência de nanossegundos em todo o projeto.
- **features:performance**: Contém a implementação real (`AppHealthTrackerImpl`), coletores de recursos e monitors de UI.

## 2. Fluxo de Inicialização Segura (Safe Initializers)
A inicialização de módulos é feita através da interface `ModuleInitializer`. 
O `AppHealthTracker` gerencia o carregamento com as seguintes proteções:
- **Detecção de Ciclo**: Impede que o Módulo A carregue o B que carrega o A (previne ANR).
- **Idempotência**: Garante que cada módulo inicie apenas uma vez.
- **Isolamento**: Falhas em um módulo não impedem o carregamento dos outros.

**Como adicionar um novo módulo:**
1. Implemente `ModuleInitializer`.
2. Mapeie no Hilt usando `@IntoMap` e `@StartupKey`.
3. Chame `appHealthTracker.load(SeuModulo::class.java)` na classe Application.

## 3. Monitoramento Automático
Graças ao `PerformanceActivityLifecycleCallbacks`, o monitoramento de Activities é automático:
- **Render Time**: Medido do `onCreate` até o primeiro quadro.
- **Jank Detection**: Via `JankStats` em cada janela.
- **Memory Tracking**: Coletado automaticamente no `onResume`.

## 4. Consistência de Tempo
Sempre utilize `TimeUtils.nowNanos()` para medições. O sistema utiliza `SystemClock.elapsedRealtimeNanos()` por padrão para garantir precisão mesmo com o dispositivo em repouso.
