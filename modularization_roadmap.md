# Roadmap de Refatoração e Modularização Profissional

O objetivo é transformar o projeto em uma estrutura onde os módulos `:core` sejam independentes e as `:features` sejam plugáveis.

## Fase 1: Padronização do Core (`:core:common` & `:core:ui`)
- [x] **1.1. Abstração da BaseActivity**: Mover lógica de performance da `BaseActivity` para uma interface ou delegado, permitindo que a `BaseActivity` exista no `:core:common` sem depender de módulos de feature.
- [x] **1.2. Definição de Contratos**: Criar interfaces no `:core:common` para rastreamento de métricas (ex: `PerformanceMonitor`), que serão implementadas pelo módulo de performance.
- [x] **1.3. Design System no `:core:ui`**: Mover todos os componentes visuais, cores e temas do `:app` para o `:core:ui`.

## Fase 2: Isolamento da Feature Performance (`:features:performance`)
- [x] **2.1. Desacoplamento de Implementação**: O módulo de performance deve implementar as interfaces definidas no `:core:common`.
- [x] **2.2. Encapsulamento de UI**: A tela de dashboard de performance agora é acessível via rotas desacopladas, minimizando o conhecimento do `:app` sobre detalhes internos.

## Fase 3: Limpeza do Módulo `:app`
- [x] **3.1. Delegar Responsabilidades**: Uso de Hilt Multibindings para inicialização dinâmica de módulos, removendo importações diretas na `CoreApplication`.
- [x] **3.2. Orquestração**: A `MainActivity` e o `CoreApp` foram reduzidos ao mínimo necessário, focando apenas na orquestração de navegação e tema.

## Fase 4: Preparação para Módulos Externos (ex: `GamesData`)
- [x] **4.1. Publicação Local**: Configurar scripts para permitir que `:core` e `:features` sejam consumidos como bibliotecas AAR se necessário.
- [x] **4.2. Base SDK**: Garantir que a `BaseActivity` e utilitários no `:core:common` não tenham referências a `br.com.coderednt.coreapp` fixas, facilitando o reuso.
