# 6. Convenções de Documentação

A documentação é tratada como parte do código. Seguimos padrões que garantem que o SDK seja fácil de entender e utilizar por outros desenvolvedores.

## KDoc
Obrigatório para todas as APIs públicas (classes, interfaces, funções e propriedades).
*   **Descrição**: Explique o propósito da API.
*   **Parâmetros**: Utilize `@param` para descrever entradas.
*   **Retorno**: Utilize `@return` para descrever a saída.
*   **Exemplos**: Utilize blocos de código (markdown) para demonstrar o uso se a API for complexa.

## Clean Code vs Comentários
Priorizamos código auto-explicativo.
*   **O que**: O código deve dizer o que está fazendo através de nomes claros.
*   **Por que**: Utilize comentários apenas para explicar o motivo de uma decisão técnica não óbvia ou restrição de framework.

---
[⬅️ Voltar ao Índice](../../ARCHITECTURE_GUIDE.md) | [Anterior: Qualidade e Testes](05-quality.md)
