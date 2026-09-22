package io.edusoliveira.meudia.ui.finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import io.edusoliveira.meudia.data.db.BillCategory
import io.edusoliveira.meudia.ui.finance.IconLibrary
import io.edusoliveira.meudia.ui.finance.color
import io.edusoliveira.meudia.ui.finance.icon
import io.edusoliveira.meudia.ui.theme.HairlineBorderStrong
import java.time.YearMonth

/** Campo de texto redondo, na paleta do app — nunca parece um formulário padrão. */
@Composable
fun FuturisticTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: ImageVector? = null,
) {
    val textColor = MaterialTheme.colorScheme.onSurface
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .border(1.dp, HairlineBorderStrong, RoundedCornerShape(20.dp))
            .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (leadingIcon != null) {
                Icon(
                    leadingIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(end = 10.dp),
                )
            }
            Box {
                if (value.isEmpty()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    textStyle = LocalTextStyle.current.copy(
                        color = textColor,
                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                    ),
                    cursorBrush = androidx.compose.ui.graphics.SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(keyboardType = keyboardType),
                    visualTransformation = VisualTransformation.None,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}

/** Fileira horizontal com um círculo colorido por categoria — sem dropdown. */
@Composable
fun CategoryPickerRow(
    selected: BillCategory,
    onSelect: (BillCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        BillCategory.entries.forEach { categoria ->
            val isSelected = categoria == selected
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.clickableNoRipple { onSelect(categoria) },
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) categoria.color().copy(alpha = 0.28f) else MaterialTheme.colorScheme.surfaceVariant)
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) categoria.color() else HairlineBorderStrong,
                            shape = CircleShape,
                        ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(categoria.icon(), contentDescription = categoria.label, tint = categoria.color())
                }
                Text(
                    text = categoria.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) categoria.color() else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Grade horizontal de ícones + opção de escolher uma foto. */
@Composable
fun IconOrPhotoPickerRow(
    selectedIconKey: String?,
    hasPhoto: Boolean,
    onSelectIcon: (String) -> Unit,
    onPickPhoto: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(
                    if (hasPhoto) MaterialTheme.colorScheme.primary.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surfaceVariant,
                )
                .border(
                    width = if (hasPhoto) 2.dp else 1.dp,
                    color = if (hasPhoto) MaterialTheme.colorScheme.primary else HairlineBorderStrong,
                    shape = CircleShape,
                )
                .clickableNoRipple(onPickPhoto),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.AddAPhoto, contentDescription = "Escolher foto", tint = MaterialTheme.colorScheme.onSurface)
        }
        IconLibrary.entries.forEach { entry ->
            val isSelected = !hasPhoto && entry.key == selectedIconKey
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surfaceVariant,
                    )
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else HairlineBorderStrong,
                        shape = CircleShape,
                    )
                    .clickableNoRipple { onSelectIcon(entry.key) },
                contentAlignment = Alignment.Center,
            ) {
                Icon(entry.icon, contentDescription = entry.label, tint = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

/** Chips de mês, multi-seleção — usado pra agendar uma conta em vários meses de uma vez. */
@Composable
fun MonthChipsSelector(
    meses: List<YearMonth>,
    selecionados: Set<YearMonth>,
    onToggle: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        meses.forEach { mes ->
            val isSelected = mes in selecionados
            val label = "${mes.month.getDisplayName(java.time.format.TextStyle.SHORT, java.util.Locale("pt", "BR"))}/${mes.year.toString().takeLast(2)}"
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.24f) else MaterialTheme.colorScheme.surfaceVariant)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else HairlineBorderStrong,
                        shape = RoundedCornerShape(14.dp),
                    )
                    .clickableNoRipple { onToggle(mes) }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label.replaceFirstChar { it.uppercase() },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier = this.then(
    Modifier.clickable(
        indication = null,
        interactionSource = androidx.compose.runtime.remember { androidx.compose.foundation.interaction.MutableInteractionSource() },
        onClick = onClick,
    ),
)
