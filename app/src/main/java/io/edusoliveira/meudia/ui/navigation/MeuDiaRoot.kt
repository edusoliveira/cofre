package io.edusoliveira.meudia.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import io.edusoliveira.meudia.ui.ViewModelFactory
import io.edusoliveira.meudia.ui.finance.FinanceDashboardScreen
import io.edusoliveira.meudia.ui.finance.FinanceViewModel
import io.edusoliveira.meudia.ui.finance.HistoryScreen
import io.edusoliveira.meudia.ui.finance.HistoryViewModel
import io.edusoliveira.meudia.ui.finance.MyBillsScreen
import io.edusoliveira.meudia.ui.finance.RecurringBillsViewModel
import io.edusoliveira.meudia.ui.theme.HairlineBorderStrong
import io.edusoliveira.meudia.ui.theme.SilverMuted
import io.edusoliveira.meudia.ui.theme.White

private enum class MeuDiaTab(val label: String, val icon: ImageVector) {
    DASHBOARD("Início", Icons.Filled.Home),
    BILLS("Contas", Icons.Filled.CreditCard),
    HISTORY("Histórico", Icons.Filled.History),
}

@Composable
fun MeuDiaRoot(factory: ViewModelFactory) {
    var selectedTab by remember { mutableStateOf(MeuDiaTab.DASHBOARD) }

    Box(modifier = Modifier.background(MaterialTheme.colorScheme.background)) {
        Box(modifier = Modifier.padding(bottom = 88.dp)) {
            when (selectedTab) {
                MeuDiaTab.DASHBOARD -> {
                    val viewModel: FinanceViewModel = viewModel(factory = factory)
                    FinanceDashboardScreen(viewModel = viewModel)
                }
                MeuDiaTab.BILLS -> {
                    val viewModel: RecurringBillsViewModel = viewModel(factory = factory)
                    MyBillsScreen(viewModel = viewModel)
                }
                MeuDiaTab.HISTORY -> {
                    val viewModel: HistoryViewModel = viewModel(factory = factory)
                    HistoryScreen(viewModel = viewModel)
                }
            }
        }

        FloatingDock(
            selectedTab = selectedTab,
            onSelectTab = { selectedTab = it },
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun FloatingDock(
    selectedTab: MeuDiaTab,
    onSelectTab: (MeuDiaTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        shape = RoundedCornerShape(28.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, HairlineBorderStrong),
        tonalElevation = 8.dp,
        shadowElevation = 12.dp,
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                MeuDiaTab.entries.forEach { tab ->
                    DockItem(
                        tab = tab,
                        selected = tab == selectedTab,
                        onClick = { onSelectTab(tab) },
                    )
                }
            }
        }
    }
}

@Composable
private fun DockItem(
    tab: MeuDiaTab,
    selected: Boolean,
    onClick: () -> Unit,
) {
    // Estado ativo: pastilha prata translúcida só atrás do ícone, sem
    // animação contínua — o dock não redesenha nada enquanto está parado.
    val contentColor = if (selected) White else SilverMuted
    val pillColor = if (selected) Color.White.copy(alpha = 0.10f) else Color.Transparent

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(pillColor),
            contentAlignment = Alignment.Center,
        ) {
            Icon(tab.icon, contentDescription = tab.label, tint = contentColor, modifier = Modifier.size(22.dp))
        }
        Text(
            text = tab.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            color = contentColor,
        )
    }
}
