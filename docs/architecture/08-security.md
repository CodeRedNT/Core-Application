# 8. Segurança e Hardening

A segurança de dados é um pilar fundamental do Core-Application. Implementamos mecanismos para garantir que informações sensíveis sejam protegidas contra acesso não autorizado.

## Armazenamento Seguro (`EncryptedSharedPreferences`)
Utilizamos o **Jetpack Security** para criptografar dados em repouso no dispositivo.

### Principais Características:
*   **Chaves Gerenciadas**: As chaves de criptografia são armazenadas no Android Keystore, protegidas por hardware sempre que possível.
*   **AES-256**: Utilizamos os esquemas AES256_SIV para chaves e AES256_GCM para valores.
*   **Abstração via Domínio**: O acesso ao armazenamento seguro é feito através da interface `SecureStorageRepository`, garantindo que a lógica de negócio não dependa da implementação técnica de segurança.

### Exemplo de Uso:
```kotlin
@Inject lateinit var secureStorage: SecureStorageRepository

suspend fun handleLogin(token: String) {
    // Salva o token criptografado
    secureStorage.saveString("auth_token", token)
}
```

## Como Integrar:
1.  Injete o `SecureStorageRepository` no seu UseCase ou ViewModel.
2.  Utilize os métodos `saveString` e `getString` para dados que não podem ser armazenados em texto puro.

---
[⬅️ Voltar ao Índice](../../ARCHITECTURE_GUIDE.md) | [Anterior: Logging](07-logging.md)
