package io.edusoliveira.meudia.data

import java.time.YearMonth

data class MonthSummary(
    val mesAno: YearMonth,
    val totalContas: Long,
    val totalPago: Long,
    val totalRecebido: Long,
) {
    val saldo: Long get() = totalRecebido - totalContas
}
