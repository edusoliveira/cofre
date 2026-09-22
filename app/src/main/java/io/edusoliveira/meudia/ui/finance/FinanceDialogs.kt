@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package io.edusoliveira.meudia.ui.finance

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import io.edusoliveira.meudia.data.db.BillCategory
import io.edusoliveira.meudia.data.db.BillType
import io.edusoliveira.meudia.data.db.entities.MonthBillEntryEntity
import io.edusoliveira.meudia.data.db.entities.MonthlyIncomeEntity
import io.edusoliveira.meudia.data.db.entities.RecurringBillEntity
import io.edusoliveira.meudia.ui.finance.components.CategoryPickerRow
import io.edusoliveira.meudia.ui.finance.components.FuturisticTextField
import io.edusoliveira.meudia.ui.finance.components.IconOrPhotoPickerRow
import io.edusoliveira.meudia.ui.finance.components.MonthChipsSelector
import io.edusoliveira.meudia.ui.theme.ExpenseTerracotta
import io.edusoliveira.meudia.ui.theme.HairlineBorderStrong
import io.edusoliveira.meudia.util.centsToPlainInput
import io.edusoliveira.meudia.util.copyImageToInternalStorage
import io.edusoliveira.meudia.util.parseBRLInputToCents
import kotlinx.coroutines.launch
import java.time.YearMonth

/** Casca compartilhada de todo diálogo do app: cantos bem arredondados, cor
 * da própria paleta escura, nunca o visual de formulário padrão do sistema. */
@Composable
private fun FuturisticDialogShell(
    title: String,
    onDismiss: () -> Unit,
    confirmLabel: String,
    confirmEnabled: Boolean,
    onConfirm: () -> Unit,
    deleteAction: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surface,
            border = BorderStroke(1.dp, HairlineBorderStrong),
        ) {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(20.dp))
                content()
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    if (deleteAction != null) {
                        TextButton(onClick = deleteAction) {
                            Text("Excluir", color = ExpenseTerracotta)
                        }
                    } else {
                        Spacer(modifier = Modifier)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) { Text("Cancelar") }
                        Button(
                            onClick = onConfirm,
                            enabled = confirmEnabled,
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = Color.Black,
                            ),
                        ) { Text(confirmLabel, fontWeight = FontWeight.SemiBold) }
                    }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

@Composable
fun AddExpenseDialog(
    mesAtual: YearMonth,
    onDismiss: () -> Unit,
    onConfirm: (
        nome: String,
        categoria: BillCategory,
        valorCentavos: Long,
        meses: List<YearMonth>,
        iconKey: String?,
        photoPath: String?,
    ) -> Unit,
) {
    var nome by remember { mutableStateOf("") }
    var valorTexto by remember { mutableStateOf("") }
    var categoria by remember { mutableStateOf(BillCategory.OUTRO) }
    var iconKey by remember { mutableStateOf<String?>(null) }
    var photoPath by remember { mutableStateOf<String?>(null) }
    var mesesSelecionados by remember { mutableStateOf(setOf(mesAtual)) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                copyImageToInternalStorage(context, uri)?.let { path ->
                    photoPath = path
                    iconKey = null
                }
            }
        }
    }
    val mesesDisponiveis = remember(mesAtual) { (-1..11).map { mesAtual.plusMonths(it.toLong()) } }

    FuturisticDialogShell(
        title = "Adicionar gasto",
        onDismiss = onDismiss,
        confirmLabel = "Adicionar",
        confirmEnabled = nome.isNotBlank() && valorTexto.isNotBlank() && mesesSelecionados.isNotEmpty(),
        onConfirm = {
            onConfirm(nome, categoria, parseBRLInputToCents(valorTexto), mesesSelecionados.sorted(), iconKey, photoPath)
        },
    ) {
        FuturisticTextField(value = nome, onValueChange = { nome = it }, placeholder = "Nome do gasto")
        Spacer(modifier = Modifier.height(12.dp))
        FuturisticTextField(
            value = valorTexto,
            onValueChange = { valorTexto = it },
            placeholder = "Valor (R$)",
            keyboardType = KeyboardType.Number,
        )
        Spacer(modifier = Modifier.height(18.dp))
        FieldLabel("Categoria")
        Spacer(modifier = Modifier.height(8.dp))
        CategoryPickerRow(selected = categoria, onSelect = { categoria = it })
        Spacer(modifier = Modifier.height(18.dp))
        FieldLabel("Ícone ou foto")
        Spacer(modifier = Modifier.height(8.dp))
        IconOrPhotoPickerRow(
            selectedIconKey = iconKey,
            hasPhoto = photoPath != null,
            onSelectIcon = { iconKey = it; photoPath = null },
            onPickPhoto = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        )
        Spacer(modifier = Modifier.height(18.dp))
        FieldLabel("Repetir em quais meses")
        Spacer(modifier = Modifier.height(8.dp))
        MonthChipsSelector(
            meses = mesesDisponiveis,
            selecionados = mesesSelecionados,
            onToggle = { mes ->
                mesesSelecionados = if (mes in mesesSelecionados) mesesSelecionados - mes else mesesSelecionados + mes
            },
        )
    }
}

@Composable
fun AddIncomeDialog(
    initial: MonthlyIncomeEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (descricao: String, valorCentavos: Long) -> Unit,
    onDelete: (() -> Unit)? = null,
) {
    var descricao by remember { mutableStateOf(initial?.descricao ?: "Salário") }
    var valorTexto by remember { mutableStateOf(initial?.valor?.centsToPlainInput() ?: "") }

    FuturisticDialogShell(
        title = if (initial == null) "Adicionar salário / ingresso" else "Editar entrada",
        onDismiss = onDismiss,
        confirmLabel = if (initial == null) "Adicionar" else "Salvar",
        confirmEnabled = descricao.isNotBlank() && valorTexto.isNotBlank(),
        onConfirm = { onConfirm(descricao, parseBRLInputToCents(valorTexto)) },
        deleteAction = onDelete,
    ) {
        FuturisticTextField(value = descricao, onValueChange = { descricao = it }, placeholder = "Descrição")
        Spacer(modifier = Modifier.height(12.dp))
        FuturisticTextField(
            value = valorTexto,
            onValueChange = { valorTexto = it },
            placeholder = "Valor (R$)",
            keyboardType = KeyboardType.Number,
        )
    }
}

/** Editar uma conta já lançada no mês (nome, categoria, valor) — ou excluí-la. */
@Composable
fun EditBillEntryDialog(
    entry: MonthBillEntryEntity,
    onDismiss: () -> Unit,
    onConfirm: (nome: String, categoria: BillCategory, valorCentavos: Long) -> Unit,
    onDelete: () -> Unit,
) {
    var nome by remember { mutableStateOf(entry.nome) }
    var valorTexto by remember { mutableStateOf(entry.valor.centsToPlainInput()) }
    var categoria by remember { mutableStateOf(entry.categoria) }

    FuturisticDialogShell(
        title = "Editar conta",
        onDismiss = onDismiss,
        confirmLabel = "Salvar",
        confirmEnabled = nome.isNotBlank() && valorTexto.isNotBlank(),
        onConfirm = { onConfirm(nome, categoria, parseBRLInputToCents(valorTexto)) },
        deleteAction = onDelete,
    ) {
        FuturisticTextField(value = nome, onValueChange = { nome = it }, placeholder = "Nome")
        Spacer(modifier = Modifier.height(12.dp))
        FuturisticTextField(
            value = valorTexto,
            onValueChange = { valorTexto = it },
            placeholder = "Valor (R$)",
            keyboardType = KeyboardType.Number,
        )
        Spacer(modifier = Modifier.height(18.dp))
        FieldLabel("Categoria")
        Spacer(modifier = Modifier.height(8.dp))
        CategoryPickerRow(selected = categoria, onSelect = { categoria = it })
    }
}

@Composable
fun RecurringBillFormDialog(
    title: String,
    initial: RecurringBillEntity? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        nome: String,
        categoria: BillCategory,
        valorCentavos: Long,
        tipo: BillType,
        diaVencimento: Int,
        mesVencimento: Int?,
        iconKey: String?,
        photoPath: String?,
    ) -> Unit,
) {
    var nome by remember { mutableStateOf(initial?.nome ?: "") }
    var valorTexto by remember { mutableStateOf(initial?.valorPadrao?.centsToPlainInput() ?: "") }
    var categoria by remember { mutableStateOf(initial?.categoria ?: BillCategory.OUTRO) }
    var tipo by remember { mutableStateOf(initial?.tipo ?: BillType.MENSAL) }
    var diaTexto by remember { mutableStateOf(initial?.diaVencimento?.toString() ?: "") }
    var mesTexto by remember { mutableStateOf(initial?.mesVencimento?.toString() ?: "") }
    var iconKey by remember { mutableStateOf(initial?.iconKey) }
    var photoPath by remember { mutableStateOf(initial?.photoPath) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            scope.launch {
                copyImageToInternalStorage(context, uri)?.let { path ->
                    photoPath = path
                    iconKey = null
                }
            }
        }
    }

    FuturisticDialogShell(
        title = title,
        onDismiss = onDismiss,
        confirmLabel = "Salvar",
        confirmEnabled = run {
            val dia = diaTexto.toIntOrNull()
            val mes = mesTexto.toIntOrNull()
            nome.isNotBlank() && valorTexto.isNotBlank() && dia != null && dia in 1..31 &&
                (tipo == BillType.MENSAL || (mes != null && mes in 1..12))
        },
        onConfirm = {
            val dia = diaTexto.toIntOrNull() ?: 1
            val mes = mesTexto.toIntOrNull()
            onConfirm(
                nome,
                categoria,
                parseBRLInputToCents(valorTexto),
                tipo,
                dia,
                if (tipo == BillType.ANUAL) mes else null,
                iconKey,
                photoPath,
            )
        },
    ) {
        FuturisticTextField(value = nome, onValueChange = { nome = it }, placeholder = "Nome")
        Spacer(modifier = Modifier.height(12.dp))
        FuturisticTextField(
            value = valorTexto,
            onValueChange = { valorTexto = it },
            placeholder = "Valor padrão (R$)",
            keyboardType = KeyboardType.Number,
        )
        Spacer(modifier = Modifier.height(18.dp))
        FieldLabel("Categoria")
        Spacer(modifier = Modifier.height(8.dp))
        CategoryPickerRow(selected = categoria, onSelect = { categoria = it })
        Spacer(modifier = Modifier.height(18.dp))
        FieldLabel("Ícone ou foto")
        Spacer(modifier = Modifier.height(8.dp))
        IconOrPhotoPickerRow(
            selectedIconKey = iconKey,
            hasPhoto = photoPath != null,
            onSelectIcon = { iconKey = it; photoPath = null },
            onPickPhoto = { photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        )
        Spacer(modifier = Modifier.height(18.dp))
        FieldLabel("Frequência")
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            FrequencyChip(label = "Mensal", selected = tipo == BillType.MENSAL, onClick = { tipo = BillType.MENSAL })
            FrequencyChip(label = "Anual", selected = tipo == BillType.ANUAL, onClick = { tipo = BillType.ANUAL })
        }
        Spacer(modifier = Modifier.height(18.dp))
        FuturisticTextField(
            value = diaTexto,
            onValueChange = { diaTexto = it.filter(Char::isDigit) },
            placeholder = "Dia de vencimento",
            keyboardType = KeyboardType.Number,
        )
        if (tipo == BillType.ANUAL) {
            Spacer(modifier = Modifier.height(12.dp))
            FuturisticTextField(
                value = mesTexto,
                onValueChange = { mesTexto = it.filter(Char::isDigit) },
                placeholder = "Mês de vencimento (1-12)",
                keyboardType = KeyboardType.Number,
            )
        }
    }
}

@Composable
private fun FrequencyChip(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = if (selected) MaterialTheme.colorScheme.primary.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) MaterialTheme.colorScheme.primary else HairlineBorderStrong,
        ),
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
