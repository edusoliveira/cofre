@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package io.edusoliveira.meudia.ui.finance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.edusoliveira.meudia.data.db.BillType
import io.edusoliveira.meudia.data.db.entities.RecurringBillEntity
import io.edusoliveira.meudia.ui.components.MeuDiaTopBar
import io.edusoliveira.meudia.ui.theme.HairlineBorder
import io.edusoliveira.meudia.util.centsToBRL

@Composable
fun MyBillsScreen(viewModel: RecurringBillsViewModel) {
    val bills by viewModel.bills.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }
    var editingBill by remember { mutableStateOf<RecurringBillEntity?>(null) }

    val mensais = bills.filter { it.tipo == BillType.MENSAL }
    val anuais = bills.filter { it.tipo == BillType.ANUAL }

    Scaffold(
        topBar = {
            MeuDiaTopBar {
                Text(
                    text = "Minhas contas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = "Nova conta")
            }
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            item {
                Text("Mensais", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            if (mensais.isEmpty()) {
                item {
                    Text(
                        "Nenhuma conta mensal cadastrada.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(mensais, key = { it.id }) { bill ->
                    RecurringBillRow(
                        bill = bill,
                        onClick = { editingBill = bill },
                        onToggleActive = { viewModel.setActive(bill, !bill.ativa) },
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(8.dp)) }

            item {
                Text("Anuais", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }
            if (anuais.isEmpty()) {
                item {
                    Text(
                        "Nenhuma conta anual cadastrada.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            } else {
                items(anuais, key = { it.id }) { bill ->
                    RecurringBillRow(
                        bill = bill,
                        onClick = { editingBill = bill },
                        onToggleActive = { viewModel.setActive(bill, !bill.ativa) },
                    )
                }
            }
        }
    }

    if (showAddDialog) {
        RecurringBillFormDialog(
            title = "Nova conta",
            onDismiss = { showAddDialog = false },
            onConfirm = { nome, categoria, valor, tipo, dia, mes, iconKey, photoPath ->
                viewModel.addBill(nome, categoria, valor, tipo, dia, mes, iconKey, photoPath)
                showAddDialog = false
            },
        )
    }

    editingBill?.let { bill ->
        RecurringBillFormDialog(
            title = "Editar conta",
            initial = bill,
            onDismiss = { editingBill = null },
            onConfirm = { nome, categoria, valor, tipo, dia, mes, iconKey, photoPath ->
                viewModel.updateBill(
                    bill.copy(
                        nome = nome,
                        categoria = categoria,
                        valorPadrao = valor,
                        tipo = tipo,
                        diaVencimento = dia,
                        mesVencimento = mes,
                        iconKey = iconKey,
                        photoPath = photoPath,
                    ),
                )
                editingBill = null
            },
        )
    }
}

@Composable
private fun RecurringBillRow(
    bill: RecurringBillEntity,
    onClick: () -> Unit,
    onToggleActive: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, HairlineBorder),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Row(
            modifier = Modifier
                .padding(14.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BillVisualIcon(
                categoria = bill.categoria,
                iconKey = bill.iconKey,
                photoPath = bill.photoPath,
                size = 24.dp,
            )
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(bill.nome, fontWeight = FontWeight.Medium)
                val vencimento = if (bill.tipo == BillType.ANUAL && bill.mesVencimento != null) {
                    "${bill.categoria.label} · vence dia ${bill.diaVencimento} (mês ${bill.mesVencimento})"
                } else {
                    "${bill.categoria.label} · vence dia ${bill.diaVencimento}"
                }
                Text(
                    text = vencimento,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = bill.valorPadrao.centsToBRL(),
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(end = 8.dp),
            )
            Switch(checked = bill.ativa, onCheckedChange = { onToggleActive() })
        }
    }
}
