# Roadmap de Evolução: Core Application Professional

Este documento descreve os pilares e componentes necessários para transformar o projeto em uma arquitetura multimodular de nível empresarial (Enterprise-Ready).

## 1. Infraestrutura de Dados Centralizada
- [x] **`:database`**: Configuração central do Room, gerenciamento de instâncias e migrações.
- [x] **`:datastore`**: Persistência de preferências de usuário de forma reativa com Jetpack DataStore.

## 2. Design System (`:ui`)
- [x] Definição de **Tokens** de Cores (Light/Dark mode).
- [x] Definição de **Tokens** de Espaçamento e Shapes.
- [x] Temas customizados que estendem o `MaterialTheme`.
- [x] Componentes "átomos" (CoreButton) para consistência visual em todas as features.

## 3. Governança e Qualidade de Código
- [x] **Static Analysis**: Implementação de Detekt para padronização de código.
- [x] **Custom Lint Rules**: Implementação do **Konsist** para validar regras de arquitetura e dependências entre módulos.

## 4. Gestão de Navegação Robusta (`:navigation`)
- [x] Centralização de rotas e contratos de navegação.
- [x] Desacoplamento de telas usando o padrão de `Navigator` reativo.

## 5. Analytics e Observabilidade (`:analytics`)
- [x] Interface única para log de eventos de negócio (AnalyticsHelper).
- [x] Abstração de provedores com implementação de Debug/Logcat.

## 6. Estratégia de Testes Avançada
- [x] **Test Fixtures**: Habilitado compartilhamento de dados fakes entre módulos no `:common`.
- [x] **Screenshot Testing**: Implementação do **Roborazzi** no módulo `:core:ui` para garantir regressão visual automática.

## 7. CI/CD e Automação
- [x] **Dependency Analysis**: Plugin adicionado para otimizar o grafo de dependências e tempo de build.

## 8. Arquitetura de Domínio (`:domain`)
- [x] Criado módulo **Pure Kotlin** para modelos de dados puros, repositórios e UseCases.
- [ ] Implementação de UseCases base para as features existentes.
