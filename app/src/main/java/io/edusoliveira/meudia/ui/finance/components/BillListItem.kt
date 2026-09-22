@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package io.edusoliveira.meudia.ui.finance.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import io.edusoliveira.meudia.data.db.entities.MonthBillEntryEntity
import io.edusoliveira.meudia.ui.finance.BillVisualIcon
import io.edusoliveira.meudia.ui.finance.color
import io.edusoliveira.meudia.ui.theme.ExpenseTerracotta
import io.edusoliveira.meudia.ui.theme.HairlineBorder
import io.edusoliveira.meudia.ui.theme.IncomeGreen
import io.edusoliveira.meudia.util.centsToBRL

@Composable
fun BillListItem(
    entry: MonthBillEntryEntity,
    onTogglePaid: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, HairlineBorder),
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BillVisualIcon(
                categoria = entry.categoria,
                iconKey = entry.iconKey,
                photoPath = entry.photoPath,
                size = 26.dp,
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.nome,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    textDecoration = if (entry.pago) TextDecoration.LineThrough else null,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = entry.categoria.label,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = entry.valor.centsToBRL(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = if (entry.pago) IncomeGreen else ExpenseTerracotta,
                )
                Spacer(modifier = Modifier.height(4.dp))
                FilterChip(
                    selected = entry.pago,
                    onClick = onTogglePaid,
                    label = { Text(if (entry.pago) "Pago" else "Marcar pago") },
                    leadingIcon = if (entry.pago) {
                        { Icon(Icons.Filled.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    } else {
                        null
                    },
                )
            }
        }
    }
}
