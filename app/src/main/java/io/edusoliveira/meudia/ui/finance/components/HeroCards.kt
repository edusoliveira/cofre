package io.edusoliveira.meudia.ui.finance.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.edusoliveira.meudia.ui.theme.Black
import io.edusoliveira.meudia.ui.theme.HairlineBorderStrong
import io.edusoliveira.meudia.ui.theme.Silver
import io.edusoliveira.meudia.ui.theme.SilverMuted
import io.edusoliveira.meudia.ui.theme.White
import io.edusoliveira.meudia.util.centsToBRL
import kotlin.math.roundToInt

private val HeroCardShape = RoundedCornerShape(24.dp)

/**
 * Brilho do card: um clarao de grafite saindo de um canto e morrendo no preto.
 * E desenhado uma unica vez por tamanho (drawWithCache) e nao anima -- o app
 * nao tem mais animacao continua, que era o que consumia CPU.
 */
private fun Modifier.heroSheen(fromTopStart: Boolean): Modifier =
    this.drawWithCache {
        val origin = if (fromTopStart) {
            Offset(size.width * 0.10f, size.height * 0.05f)
        } else {
            Offset(size.width * 0.90f, size.height * 0.05f)
        }
        val brush = Brush.radialGradient(
            colors = listOf(Color(0xFF34373B), Color(0xFF17181A), Black),
            center = origin,
            radius = size.width * 1.15f,
        )
        onDrawBehind { drawRect(brush = brush) }
    }

/** Card "hero": preto com brilho prata sutil, cantos bem arredondados e borda
 * em fio de cabelo -- sem cor e sem animacao. */
@Composable
private fun HeroCard(
    sheenFromTopStart: Boolean,
    modifier: Modifier = Modifier,
    topEndBadge: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .clip(HeroCardShape)
            .background(Black)
            .heroSheen(sheenFromTopStart)
            .border(1.dp, HairlineBorderStrong, HeroCardShape),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(22.dp),
            content = content,
        )
        if (topEndBadge != null) {
            Box(modifier = Modifier.align(Alignment.TopEnd).padding(16.dp)) {
                topEndBadge()
            }
        }
    }
}

@Composable
fun HeroCardsCarousel(
    saldo: Long,
    totalRecebido: Long,
    totalContas: Long,
    variacaoPercentual: Float?,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(end = 48.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            BalanceHeroCard(
                saldo = saldo,
                variacaoPercentual = variacaoPercentual,
                modifier = Modifier.fillParentMaxWidth(0.86f),
            )
        }
        item {
            IncomeHeroCard(
                totalRecebido = totalRecebido,
                totalContas = totalContas,
                modifier = Modifier.fillParentMaxWidth(0.86f),
            )
        }
    }
}

@Composable
private fun BalanceHeroCard(
    saldo: Long,
    variacaoPercentual: Float?,
    modifier: Modifier = Modifier,
) {
    HeroCard(
        sheenFromTopStart = true,
        modifier = modifier,
        topEndBadge = if (variacaoPercentual != null) {
            { VariationChip(variacaoPercentual) }
        } else {
            null
        },
    ) {
        Text(text = "Saldo do mês", color = SilverMuted, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = saldo.centsToBRL(),
            color = White,
            fontSize = 38.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall,
        )
    }
}

@Composable
private fun IncomeHeroCard(
    totalRecebido: Long,
    totalContas: Long,
    modifier: Modifier = Modifier,
) {
    HeroCard(
        sheenFromTopStart = false,
        modifier = modifier,
    ) {
        Text(text = "Entradas do mês", color = SilverMuted, style = MaterialTheme.typography.bodyMedium)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = totalRecebido.centsToBRL(),
            color = White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.displaySmall,
        )
        Spacer(modifier = Modifier.height(18.dp))
        Text(
            text = "Contas do mês: ${totalContas.centsToBRL()}",
            color = Silver,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun VariationChip(percentual: Float, modifier: Modifier = Modifier) {
    val positivo = percentual >= 0
    val icon: ImageVector = if (positivo) Icons.Filled.ArrowUpward else Icons.Filled.ArrowDownward
    val color = if (positivo) White else SilverMuted
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(50),
        color = Color.White.copy(alpha = 0.10f),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
            Text(
                text = "${kotlin.math.abs(percentual).roundToInt()}%",
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                softWrap = false,
                fontWeight = FontWeight.Bold,
                color = color,
            )
        }
    }
}
