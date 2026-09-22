package io.edusoliveira.meudia.util

import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val monthKeyFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM")

/** Chave estável usada para persistir o mês no banco, ex: "2026-04". */
fun YearMonth.toKey(): String = format(monthKeyFormatter)

fun String.toYearMonth(): YearMonth = YearMonth.parse(this, monthKeyFormatter)

/** Rótulo amigável para exibição, ex: "Abril de 2026". */
fun YearMonth.displayLabel(): String {
    val nomeMes = month.getDisplayName(TextStyle.FULL, Locale("pt", "BR"))
        .replaceFirstChar { it.uppercase() }
    return "$nomeMes de $year"
}
