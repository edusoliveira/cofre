package io.edusoliveira.meudia.ui.finance.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.edusoliveira.meudia.ui.finance.CategoryBreakdownItem
import io.edusoliveira.meudia.ui.finance.color

/**
 * Mini gráfico de barras verticais comparando o gasto por categoria no mês,
 * desenhado com Canvas puro (sem biblioteca de gráficos). Complementa a lista
 * detalhada de "Por categoria", dando uma leitura visual rápida.
 */
@Composable
fun CategoryBarChart(
    items: List<CategoryBreakdownItem>,
    modifier: Modifier = Modifier,
) {
    if (items.isEmpty()) return
    val maiorValor = items.maxOf { it.total }.coerceAtLeast(1)

    Column(modifier = modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .padding(horizontal = 4.dp),
        ) {
            val barCount = items.size
            val spacing = 12.dp.toPx()
            val totalSpacing = spacing * (barCount + 1)
            val barWidth = ((size.width - totalSpacing) / barCount).coerceAtLeast(8f)
            val cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())

            items.forEachIndexed { index, item ->
                val barHeight = size.height * (item.total.toFloat() / maiorValor.toFloat())
                val left = spacing + index * (barWidth + spacing)
                val top = size.height - barHeight

                val path = Path().apply {
                    addRoundRect(
                        androidx.compose.ui.geometry.RoundRect(
                            rect = androidx.compose.ui.geometry.Rect(
                                offset = Offset(left, top),
                                size = Size(barWidth, barHeight.coerceAtLeast(4f)),
                            ),
                            topLeft = cornerRadius,
                            topRight = cornerRadius,
                        ),
                    )
                }
                drawPath(path = path, color = item.categoria.color())
            }
        }
        CategoryLabelsRow(items = items)
    }
}

@Composable
private fun CategoryLabelsRow(items: List<CategoryBreakdownItem>) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        items.forEach { item ->
            Text(
                text = item.categoria.label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
        }
    }
}
