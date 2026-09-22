package io.edusoliveira.meudia.ui.finance

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Subscriptions
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import io.edusoliveira.meudia.data.db.BillCategory
import io.edusoliveira.meudia.util.rememberBitmapFromPath
import io.edusoliveira.meudia.ui.theme.CategoryAssinatura
import io.edusoliveira.meudia.ui.theme.CategoryCartao
import io.edusoliveira.meudia.ui.theme.CategoryDoacao
import io.edusoliveira.meudia.ui.theme.CategoryEducacao
import io.edusoliveira.meudia.ui.theme.CategoryEmprestimo
import io.edusoliveira.meudia.ui.theme.CategoryMoradia
import io.edusoliveira.meudia.ui.theme.CategoryOutro
import io.edusoliveira.meudia.ui.theme.CategoryTransporte
import io.edusoliveira.meudia.ui.theme.CategoryViagem

fun BillCategory.color(): Color = when (this) {
    BillCategory.MORADIA -> CategoryMoradia
    BillCategory.TRANSPORTE -> CategoryTransporte
    BillCategory.ASSINATURA -> CategoryAssinatura
    BillCategory.EMPRESTIMO -> CategoryEmprestimo
    BillCategory.DOACAO -> CategoryDoacao
    BillCategory.EDUCACAO -> CategoryEducacao
    BillCategory.CARTAO -> CategoryCartao
    BillCategory.VIAGEM -> CategoryViagem
    BillCategory.OUTRO -> CategoryOutro
}

fun BillCategory.icon(): ImageVector = when (this) {
    BillCategory.MORADIA -> Icons.Filled.Home
    BillCategory.TRANSPORTE -> Icons.Filled.DirectionsCar
    BillCategory.ASSINATURA -> Icons.Filled.Subscriptions
    BillCategory.EMPRESTIMO -> Icons.Filled.AccountBalance
    BillCategory.DOACAO -> Icons.Filled.VolunteerActivism
    BillCategory.EDUCACAO -> Icons.Filled.School
    BillCategory.CARTAO -> Icons.Filled.CreditCard
    BillCategory.VIAGEM -> Icons.Filled.Flight
    BillCategory.OUTRO -> Icons.Filled.Category
}

/**
 * Ícone/foto de uma conta: mostra a foto se houver, senão o ícone
 * personalizado escolhido, senão o ícone padrão da categoria.
 */
@Composable
fun BillVisualIcon(
    categoria: BillCategory,
    iconKey: String?,
    photoPath: String?,
    size: Dp,
    modifier: Modifier = Modifier,
) {
    val bitmap = rememberBitmapFromPath(photoPath)
    if (photoPath != null && bitmap != null) {
        Image(
            bitmap = bitmap,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
        )
    } else {
        val icon = IconLibrary.byKey(iconKey) ?: categoria.icon()
        Icon(
            icon,
            contentDescription = null,
            tint = categoria.color(),
            modifier = modifier.size(size),
        )
    }
}
