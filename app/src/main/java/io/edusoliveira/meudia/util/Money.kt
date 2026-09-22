package io.edusoliveira.meudia.util

import java.text.NumberFormat
import java.util.Locale
import kotlin.math.roundToLong

private fun brFormat(): NumberFormat = NumberFormat.getCurrencyInstance(Locale("pt", "BR"))

/** Converte um valor em centavos (Long) para o formato "R$ 1.234,56". */
fun Long.centsToBRL(): String = brFormat().format(this / 100.0)

/** Converte centavos para um texto editável simples, ex: "1234,56". */
fun Long.centsToPlainInput(): String {
    val negative = this < 0
    val abs = kotlin.math.abs(this)
    val reais = abs / 100
    val centavos = abs % 100
    val sign = if (negative) "-" else ""
    return "$sign$reais,${centavos.toString().padStart(2, '0')}"
}

/** Interpreta um texto digitado pelo usuário (ex: "1.234,56" ou "1234,56") como centavos. */
fun parseBRLInputToCents(input: String): Long {
    val normalized = input
        .trim()
        .replace("R$", "")
        .replace(".", "")
        .replace(",", ".")
    val value = normalized.toDoubleOrNull() ?: return 0
    return (value * 100).roundToLong()
}
