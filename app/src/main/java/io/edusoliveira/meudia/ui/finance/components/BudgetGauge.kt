package io.edusoliveira.meudia.ui.finance.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import io.edusoliveira.meudia.ui.theme.ExpenseTerracotta
import io.edusoliveira.meudia.ui.theme.Gold
import io.edusoliveira.meudia.ui.theme.IncomeGreen
import kotlin.math.roundToInt

/**
 * Gauge circular mostrando quanto do total de contas do mês já foi pago
 * (progresso = totalPago / totalContas). Desenhado com Canvas, sem
 * biblioteca de gráficos.
 */
@Composable
fun BudgetGauge(
    progresso: Float,
    modifier: Modifier = Modifier,
    size: androidx.compose.ui.unit.Dp = 96.dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 10.dp,
) {
    val trackColor = MaterialTheme.colorScheme.surfaceVariant
    val progressColor = when {
        progresso >= 1f -> IncomeGreen
        progresso >= 0.5f -> Gold
        else -> ExpenseTerracotta
    }

    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            val diameter = kotlin.math.min(this.size.width, this.size.height) - stroke.width
            val topLeft = androidx.compose.ui.geometry.Offset(
                (this.size.width - diameter) / 2f,
                (this.size.height - diameter) / 2f,
            )
            val arcSize = Size(diameter, diameter)

            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * progresso.coerceIn(0f, 1f),
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = stroke,
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "${(progresso.coerceIn(0f, 1f) * 100).roundToInt()}%",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = "pago",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
