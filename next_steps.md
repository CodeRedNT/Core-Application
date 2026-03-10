# 🚀 Próximos Passos: Evolução do Core-Application SDK

Este documento apresenta uma análise técnica do estado atual do projeto e mapeia as melhorias necessárias para elevar o SDK a um nível de maturidade profissional comparável aos grandes players do mercado.

## 📊 1. Observabilidade e Telemetria Remota
Atualmente, o módulo `:monitoring` e `:performance` coletam dados locais. Para um ambiente produtivo, precisamos de:
- [x] **Logging Estruturado**: Criar um módulo `:core:logging` utilizando uma abstração sobre o **Timber** para logs que mudam de comportamento entre Debug (Logcat) e Release (Crashlytics).
- [x] **Rastreamento de Erros Silenciosos**: Capturar exceções não fatais dentro dos inicializadores de módulos para entender falhas parciais no boot.
- [ ] **Integração com Backend**: Implementar um sistema de "Push" para enviar as métricas consolidadas (`HealthMetrics`) para um endpoint (Firebase, Sentry ou Backend proprietário).

## 🛡️ 2. Segurança e Hardening
Um SDK profissional deve garantir a integridade dos dados e do ambiente:
- [x] **Segurança de Armazenamento**: Implementar o **Jetpack Security (EncryptedSharedPreferences)** no módulo `:core:datastore` para dados sensíveis.
- [ ] **Core Network**: Criar `:core:network` com suporte nativo a **Certificate Pinning** e interceptores de segurança.
- [x] **Root/Emulator Detection**: Adicionar detecção de ambiente inseguro no rastreador de saúde para invalidar métricas de performance suspeitas.

## 🛠️ 3. Developer Experience (DX) e Tooling
Melhorar a produtividade de quem consome o SDK:
- [ ] **Custom Lint Rules**: Criar regras de lint customizadas (usando a API do Android Lint) para garantir que toda nova Activity herde de `BaseActivity`.
- [ ] **Module Templates**: Scripts para geração de novos módulos de feature seguindo os padrões do projeto.
- [ ] **Mock Data Generator**: Facilitar testes e previews com um gerador de dados randômicos para `HealthMetrics`.

## 🏗️ 4. Infraestrutura e CI/CD
Automação e estabilidade do pipeline:
- [ ] **Binary Compatibility**: Utilizar o plugin de *Binary Compatibility Validator* da Jetpack para garantir que mudanças no Core não quebrem apps que já utilizam versões anteriores.
- [ ] **GitHub Actions/Bitrise**: Configurar pipeline para rodar `detekt`, `unitTests` e `verifyRoborazzi` em cada Pull Request.
- [ ] **Geração de Artefatos**: Configurar o plugin `maven-publish` para gerar arquivos `.aar` e publicar em um repositório privado.

## 🎨 5. Design System Progresivo
O módulo `:core:ui` está no início:
- [ ] **Catálogo de Componentes**: Expandir para incluir `CoreTextField`, `CoreCard`, `CoreBottomSheet`, etc.
- [ ] **Multi-Theme Support**: Adaptar o `CoreAppTheme` para suportar *Dynamic Color* (Android 12+) e possíveis temas baseados em marca (White label).

## 🧪 6. Resiliência de Runtime
- [ ] **LeakCanary Integration**: Integrar o LeakCanary em builds de Debug para detectar vazamentos de memória automaticamente no monitor.
- [ ] **Strict Mode**: Habilitar `StrictMode` no `BaseApplication` (Debug) para detectar operações de I/O na thread principal que podem causar ANRs não detectados pelo JankStats.

---
## 📈 Tabela de Priorização

| Prioridade | Tarefa | Impacto |
| :--- | :--- | :--- |
| **Crítica** | Telemetria Remota (Push Metrics) | Alta (Visibilidade de Produção) |
| **Alta** | CI/CD Pipeline (Automated Tests) | Média (Estabilidade do Time) |
| **Alta** | Segurança (Encrypted Storage) | Alta (Conformidade LGPD/Segurança) |
| **Média** | Custom Lint Rules | Baixa (Padronização de Código) |
