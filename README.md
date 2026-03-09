# Core-Application SDK

O **Core-Application** é um ecossistema modular e robusto projetado para fornecer uma base sólida para aplicativos Android, com foco especial em monitoramento de performance, saúde da aplicação e arquitetura limpa.

## 🏗️ Arquitetura

O projeto adota uma estratégia de **Modularização por Camadas e Funcionalidades**, permitindo alta escalabilidade, testes facilitados e tempos de build otimizados.

### Estrutura de Diretórios

- `core/`: Módulos transversais que fornecem capacidades base para todo o app.
- `features/`: Módulos que implementam fluxos de negócio ou funcionalidades específicas.
- `app/`: O orquestrador que integra todos os módulos e define a aplicação final.

---

## 📦 Módulos Principais

### 🛠️ Core Modules

| Módulo | Descrição | Exemplo de Uso |
| :--- | :--- | :--- |
| `:architecture` | Componentes base de arquitetura (BaseViewModel, State management). | `class MyViewModel : BaseViewModel<State, Event>()` |
| `:common` | Utilitários compartilhados, extensões e helpers (Tempo, Strings, etc). | `TimeUtils.nowNanos()` |
| `:monitoring` | Contratos de monitoramento e interfaces de métricas. | `interface PerformanceMonitor` |
| `:logging` | Logging estruturado com integração automática ao tracker de saúde. | `logger.e(e, "Falha ao carregar dados")` |
| `:domain` | Regras de negócio puras (Kotlin-only), UseCases e Entidades. | `class GetUserDataUseCase(...)` |
| `:ui` | Design System, componentes Compose customizados e temas. | `CoreAppTheme { ... }` |
| `:navigation` | Infraestrutura de navegação baseada em rotas e monitoramento. | `AppNavigator.navigate(Route.Profile)` |
| `:analytics` | Abstração para envio de eventos e logs de analytics. | `AnalyticsTracker.logEvent("button_click")` |
| `:database` | Persistência local utilizando Room. | `userDao.insert(user)` |
| `:datastore` | Armazenamento de preferências e pequenos estados via Jetpack DataStore. | `settingsDataStore.updateData { ... }` |

### 🚀 Feature Modules

| Módulo | Descrição |
| :--- | :--- |
| `:performance` | Implementação do SDK de monitoramento (Vitals, JankStats, Memory). |

---

## 🚦 Fluxo de Inicialização (Safe Initialization)

Para garantir que o app não sofra com ANRs ou crashes durante o startup, utilizamos um sistema de inicializadores seguros.

```kotlin
// Exemplo de implementação em um módulo
@Singleton
class MyModuleInitializer @Inject constructor() : ModuleInitializer {
    override fun init(context: Context) {
        // Lógica de inicialização leve
    }
}
```

---

## 🛠️ Como Contribuir

1. **KDoc**: Toda função pública deve ter documentação KDoc explicando parâmetros e retorno.
2. **Clean Code**: Remova comentários óbvios ou obsoletos.
3. **Responsabilidades**: Se uma classe está fazendo muito, considere mover para um UseCase ou um módulo específico.
4. **Testes**: Mantenha a cobertura de testes ao adicionar novas funcionalidades.

---

## 📖 Documentação Detalhada

- [Guia de Arquitetura](ARCHITECTURE_GUIDE.md)
- [Roadmap de Revisão](ROADMAP_REVIEW.md)
- [Próximos Passos](next_steps.md)
