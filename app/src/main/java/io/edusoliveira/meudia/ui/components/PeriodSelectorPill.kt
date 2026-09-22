@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package io.edusoliveira.meudia.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.edusoliveira.meudia.util.displayLabel
import java.time.YearMonth

/** Pill central do topo: mostra o mês selecionado e abre um menu pra pular
 * diretamente para outro mês (últimos 6 e próximos 6). */
@Composable
fun PeriodSelectorPill(
    mesAno: YearMonth,
    onSelectMonth: (YearMonth) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.clickable { expanded = true },
        shape = RoundedCornerShape(50),
        color = MaterialTheme.colorScheme.surfaceVariant,
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = mesAno.displayLabel(),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Icon(
                Icons.Filled.KeyboardArrowDown,
                contentDescription = "Selecionar mês",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(start = 4.dp),
            )
        }

        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            val opcoes = (-6..6).map { mesAno.plusMonths(it.toLong()) }
            opcoes.forEach { opcao ->
                DropdownMenuItem(
                    text = { Text(opcao.displayLabel()) },
                    onClick = {
                        onSelectMonth(opcao)
                        expanded = false
                    },
                )
            }
        }
    }
}
