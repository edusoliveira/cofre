@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package io.edusoliveira.meudia.ui.finance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.edusoliveira.meudia.data.MonthSummary
import io.edusoliveira.meudia.ui.components.MeuDiaTopBar
import io.edusoliveira.meudia.ui.theme.ExpenseTerracotta
import io.edusoliveira.meudia.ui.theme.IncomeGreen
import io.edusoliveira.meudia.util.centsToBRL
import io.edusoliveira.meudia.util.displayLabel

@Composable
fun HistoryScreen(viewModel: HistoryViewModel) {
    val summaries by viewModel.summaries.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            MeuDiaTopBar {
                Text(
                    text = "Histórico",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
    ) { padding ->
        if (summaries.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text("Nenhum mês registrado ainda.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(vertical = 12.dp),
            ) {
                items(summaries, key = { it.mesAno.toString() }) { summary ->
                    MonthSummaryRow(summary)
                }
            }
        }
    }
}

@Composable
private fun MonthSummaryRow(summary: MonthSummary) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        tonalElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = summary.mesAno.displayLabel(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp)) {
                HistoryStat(label = "Recebido", value = summary.totalRecebido, color = IncomeGreen)
                HistoryStat(label = "Pago", value = summary.totalPago, color = MaterialTheme.colorScheme.onSurface)
                HistoryStat(
                    label = "Saldo",
                    value = summary.saldo,
                    color = if (summary.saldo >= 0) IncomeGreen else ExpenseTerracotta,
                )
            }
        }
    }
}

@Composable
private fun HistoryStat(label: String, value: Long, color: Color) {
    Column {
        Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value.centsToBRL(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, color = color)
    }
}
