@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package io.edusoliveira.meudia.ui.finance

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import io.edusoliveira.meudia.data.db.entities.MonthBillEntryEntity
import io.edusoliveira.meudia.data.db.entities.MonthlyIncomeEntity
import io.edusoliveira.meudia.ui.components.MeuDiaTopBar
import io.edusoliveira.meudia.ui.components.PeriodSelectorPill
import io.edusoliveira.meudia.ui.finance.components.BillListItem
import io.edusoliveira.meudia.ui.finance.components.BudgetGauge
import io.edusoliveira.meudia.ui.finance.components.CategoryBarChart
import io.edusoliveira.meudia.ui.finance.components.CategoryBreakdownList
import io.edusoliveira.meudia.ui.finance.components.HeroCardsCarousel
import io.edusoliveira.meudia.ui.finance.components.QuickActionButton
import io.edusoliveira.meudia.ui.theme.BgDeep
import io.edusoliveira.meudia.ui.theme.BgGlow
import io.edusoliveira.meudia.ui.theme.HairlineBorder
import io.edusoliveira.meudia.ui.theme.IncomeGreen
import io.edusoliveira.meudia.util.centsToBRL
import kotlinx.coroutines.launch

@Composable
fun FinanceDashboardScreen(viewModel: FinanceViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    var showExpenseDialog by remember { mutableStateOf(false) }
    var showIncomeDialog by remember { mutableStateOf(false) }
    var editingEntry by remember { mutableStateOf<MonthBillEntryEntity?>(null) }
    var editingIncome by remember { mutableStateOf<MonthlyIncomeEntity?>(null) }

    // A lista é montada condicionalmente, então a posição do cabeçalho
    // "Contas deste mês" (alvo da ação rápida) precisa ser calculada.
    val hasSummary = uiState.totalContas > 0
    val incomeRows = if (uiState.incomes.isEmpty()) 1 else uiState.incomes.size
    val billsHeaderIndex = 1 + (if (hasSummary) 1 else 0) + 1 + 1 + incomeRows

    Scaffold(
        modifier = Modifier.homeGlowBackground(),
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onBackground,
        topBar = {
            MeuDiaTopBar {
                PeriodSelectorPill(mesAno = uiState.mesAno, onSelectMonth = viewModel::goToMonth)
            }
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
        ) {
            item {
                HeroCardsCarousel(
                    saldo = uiState.saldo,
                    totalRecebido = uiState.totalRecebido,
                    totalContas = uiState.totalContas,
                    variacaoPercentual = uiState.variacaoPercentual,
                    modifier = Modifier.padding(start = 16.dp),
                )
            }

            if (hasSummary) {
                item {
                    MonthSummaryCard(
                        progressoOrcamento = uiState.progressoOrcamento,
                        breakdown = uiState.breakdown,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    QuickActionButton(
                        icon = Icons.Filled.AddCircle,
                        label = "Adicionar Gasto",
                        onClick = { showExpenseDialog = true },
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionButton(
                        icon = Icons.Filled.Payments,
                        label = "Adicionar Salário",
                        onClick = { showIncomeDialog = true },
                        modifier = Modifier.weight(1f),
                    )
                    QuickActionButton(
                        icon = Icons.AutoMirrored.Filled.ReceiptLong,
                        label = "Contas do Mês",
                        onClick = { scope.launch { listState.animateScrollToItem(billsHeaderIndex) } },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item {
                Text(
                    text = "Salário e entradas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            if (uiState.incomes.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum salário lançado neste mês. Use \"Adicionar Salário\" acima.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            } else {
                items(uiState.incomes, key = { "income-${it.id}" }) { income ->
                    IncomeListItem(
                        income = income,
                        onClick = { editingIncome = income },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            item {
                Text(
                    text = "Contas deste mês",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            if (uiState.entries.isEmpty()) {
                item {
                    Text(
                        text = "Nenhuma conta lançada neste mês ainda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            } else {
                items(uiState.entries, key = { "entry-${it.id}" }) { entry ->
                    BillListItem(
                        entry = entry,
                        onTogglePaid = { viewModel.toggleEntryPaid(entry) },
                        onClick = { editingEntry = entry },
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
            }

            if (uiState.breakdown.isNotEmpty()) {
                item {
                    Text(
                        text = "Por categoria",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 16.dp),
                    )
                }
                item {
                    CategoryBreakdownList(items = uiState.breakdown, modifier = Modifier.padding(horizontal = 16.dp))
                }
            }
        }
    }

    if (showExpenseDialog) {
        AddExpenseDialog(
            mesAtual = uiState.mesAno,
            onDismiss = { showExpenseDialog = false },
            onConfirm = { nome, categoria, valor, meses, iconKey, photoPath ->
                viewModel.addExpense(nome, categoria, valor, meses, iconKey, photoPath)
                showExpenseDialog = false
            },
        )
    }

    if (showIncomeDialog) {
        AddIncomeDialog(
            onDismiss = { showIncomeDialog = false },
            onConfirm = { descricao, valor ->
                viewModel.addIncome(descricao, valor)
                showIncomeDialog = false
            },
        )
    }

    editingEntry?.let { entry ->
        EditBillEntryDialog(
            entry = entry,
            onDismiss = { editingEntry = null },
            onConfirm = { nome, categoria, valor ->
                viewModel.updateEntry(entry.copy(nome = nome, categoria = categoria, valor = valor))
                editingEntry = null
            },
            onDelete = {
                viewModel.deleteEntry(entry)
                editingEntry = null
            },
        )
    }

    editingIncome?.let { income ->
        AddIncomeDialog(
            initial = income,
            onDismiss = { editingIncome = null },
            onConfirm = { descricao, valor ->
                viewModel.updateIncome(income.copy(descricao = descricao, valor = valor))
                editingIncome = null
            },
            onDelete = {
                viewModel.deleteIncome(income)
                editingIncome = null
            },
        )
    }
}

@Composable
private fun IncomeListItem(
    income: MonthlyIncomeEntity,
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
            Icon(Icons.Filled.Payments, contentDescription = null, tint = IncomeGreen)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = income.descricao,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = "Toque para editar",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Text(
                text = income.valor.centsToBRL(),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = IncomeGreen,
            )
        }
    }
}

@Composable
private fun MonthSummaryCard(
    progressoOrcamento: Float,
    breakdown: List<CategoryBreakdownItem>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, HairlineBorder),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BudgetGauge(progresso = progressoOrcamento)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Gasto por categoria",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                CategoryBarChart(items = breakdown)
            }
        }
    }
}

/** Gradiente radial sutil (glow no topo) atrás do conteúdo da Home/Finanças —
 * nunca uma cor chapada, conforme a direção visual do app. */
private fun Modifier.homeGlowBackground(): Modifier = this.drawWithCache {
    val brush = Brush.radialGradient(
        colors = listOf(BgGlow, BgDeep),
        center = Offset(size.width / 2f, 0f),
        radius = size.height * 0.9f,
    )
    onDrawBehind { drawRect(brush = brush) }
}
