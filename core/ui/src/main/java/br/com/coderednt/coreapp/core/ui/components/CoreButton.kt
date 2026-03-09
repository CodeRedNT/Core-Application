package br.com.coderednt.coreapp.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Componente de botão padronizado do Core-Application SDK.
 * 
 * Este botão segue o Design System do projeto, utilizando as formas e tipografias 
 * definidas no tema global. Possui uma altura padrão otimizada para toque (56dp).
 * 
 * @param text O rótulo de texto a ser exibido no botão.
 * @param onClick Ação disparada ao clicar no botão.
 * @param modifier Modificador Compose para personalizar layout e comportamento.
 * @param enabled Se falso, o botão ficará desabilitado para interação.
 * @param containerColor Cor de fundo do botão (padrão: Primary do tema).
 * @param contentColor Cor do texto/conteúdo (padrão: OnPrimary do tema).
 */
@Composable
fun CoreButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    containerColor: Color = MaterialTheme.colorScheme.primary,
    contentColor: Color = MaterialTheme.colorScheme.onPrimary
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = MaterialTheme.shapes.medium
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}
