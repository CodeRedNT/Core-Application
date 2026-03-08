# Roadmap de Evolução: Core Application Professional

Este documento descreve os pilares e componentes necessários para transformar o projeto em uma arquitetura multimodular de nível empresarial (Enterprise-Ready).

## 1. Infraestrutura de Dados Centralizada
- [ ] **`:core:network`**: Módulo isolado para Retrofit/OkHttp, autenticação (interceptor de tokens), log de requisições e tratamento global de erros de API.
- [ ] **`:core:database`**: Configuração central do Room, gerenciamento de instâncias e migrações.
- [ ] **`:core:datastore`**: Persistência de preferências de usuário de forma reativa com Jetpack DataStore.

## 2. Design System (`:core:designsystem`)
- [ ] Definição de **Tokens** (Cores, Tipografia, Espaçamento).
- [ ] Temas customizados que estendem o `MaterialTheme`.
- [ ] Componentes "átomos" (botões, cards, inputs) para consistência visual em todas as features.

## 3. Governança e Qualidade de Código
- [ ] **Static Analysis**: Implementação de Detekt e Ktlint para padronização de código.
- [ ] **Custom Lint Rules**: Regras para impedir dependências circulares ou proibidas entre módulos (ex: feature dependendo de feature).

## 4. Gestão de Navegação Robusta (`:core:navigation`)
- [ ] Centralização de rotas e contratos de navegação.
- [ ] Desacoplamento de telas usando o padrão de `Navigator` ou `Coordinator`.

## 5. Analytics e Observabilidade (`:core:analytics`)
- [ ] Abstração de provedores (Firebase, Mixpanel).
- [ ] Interface única para log de eventos de negócio.

## 6. Estratégia de Testes Avançada
- [ ] **Test Fixtures**: Compartilhamento de dados fakes entre módulos.
- [ ] **Screenshot Testing**: Implementação de Paparazzi ou Roborazzi para regressão visual no `:core:ui`.

## 7. CI/CD e Automação
- [ ] **Workflows**: GitHub Actions para rodar Lint, Testes e Build em cada PR.
- [ ] **Dependency Analysis**: Plugin para otimizar o grafo de dependências e tempo de build.

## 8. Arquitetura de Domínio (`:core:domain`)
- [ ] Repositórios, Modelos Puros e UseCases para lógica de negócio complexa.
