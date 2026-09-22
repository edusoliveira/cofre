package io.edusoliveira.meudia.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.edusoliveira.meudia.ui.theme.Gold
import io.edusoliveira.meudia.ui.theme.HeroGradientEnd

/**
 * Barra de topo com avatar (iniciais), um espaço central livre para o
 * conteúdo de cada tela (ex: seletor de período) e o ícone de notificação.
 */
@Composable
fun MeuDiaTopBar(
    modifier: Modifier = Modifier,
    center: @Composable () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        InitialsAvatar(initials = "EO")
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) { center() }
        NotificationButton()
    }
}

@Composable
private fun InitialsAvatar(initials: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(
                Brush.linearGradient(listOf(Gold, HeroGradientEnd)),
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = initials,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}

@Composable
private fun NotificationButton() {
    Box(
        modifier = Modifier
            .size(40.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f), shape = CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            Icons.Filled.Notifications,
            contentDescription = "Notificações",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp),
        )
    }
}
