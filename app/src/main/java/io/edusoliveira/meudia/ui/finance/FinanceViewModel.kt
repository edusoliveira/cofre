package io.edusoliveira.meudia.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.edusoliveira.meudia.data.FinanceRepository
import io.edusoliveira.meudia.data.MonthSummary
import io.edusoliveira.meudia.data.db.BillCategory
import io.edusoliveira.meudia.data.db.entities.MonthBillEntryEntity
import io.edusoliveira.meudia.data.db.entities.MonthlyIncomeEntity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.YearMonth
import kotlin.math.abs

data class CategoryBreakdownItem(
    val categoria: BillCategory,
    val total: Long,
    val percentual: Float,
)

data class FinanceUiState(
    val mesAno: YearMonth = YearMonth.now(),
    val entries: List<MonthBillEntryEntity> = emptyList(),
    val incomes: List<MonthlyIncomeEntity> = emptyList(),
    val totalRecebido: Long = 0,
    val totalContas: Long = 0,
    val totalPago: Long = 0,
    val variacaoPercentual: Float? = null,
    val breakdown: List<CategoryBreakdownItem> = emptyList(),
) {
    val saldo: Long get() = totalRecebido - totalContas
    val progressoOrcamento: Float
        get() = if (totalContas > 0) (totalPago.toFloat() / totalContas.toFloat()).coerceIn(0f, 1f) else 0f
}

@OptIn(ExperimentalCoroutinesApi::class)
class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val selectedMonth = MutableStateFlow(YearMonth.now())

    val uiState: StateFlow<FinanceUiState> = selectedMonth
        .flatMapLatest { mesAno ->
            combine(
                repository.observeMonthEntries(mesAno),
                repository.observeMonthIncomes(mesAno),
                flow { emit(repository.getMonthSummaryOnce(mesAno.minusMonths(1))) },
            ) { entries, incomes, mesAnterior -> buildUiState(mesAno, entries, incomes, mesAnterior) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FinanceUiState())

    init {
        // Sempre que o mês selecionado muda, garante que as contas recorrentes
        // ativas já apareçam como lançamentos daquele mês.
        viewModelScope.launch {
            selectedMonth.collectLatest { mesAno -> repository.ensureMonthSeeded(mesAno) }
        }
    }

    private fun buildUiState(
        mesAno: YearMonth,
        entries: List<MonthBillEntryEntity>,
        incomes: List<MonthlyIncomeEntity>,
        mesAnterior: MonthSummary,
    ): FinanceUiState {
        val totalContas = entries.sumOf { it.valor }
        val totalRecebido = incomes.sumOf { it.valor }
        val totalPago = entries.filter { it.pago }.sumOf { it.valor }
        val saldo = totalRecebido - totalContas
        val variacaoPercentual = if (mesAnterior.saldo != 0L) {
            ((saldo - mesAnterior.saldo).toFloat() / abs(mesAnterior.saldo).toFloat()) * 100f
        } else {
            null
        }

        val breakdown = entries
            .groupBy { it.categoria }
            .map { (categoria, list) ->
                val total = list.sumOf { it.valor }
                CategoryBreakdownItem(
                    categoria = categoria,
                    total = total,
                    percentual = if (totalContas > 0) total.toFloat() / totalContas.toFloat() else 0f,
                )
            }
            .sortedByDescending { it.total }

        return FinanceUiState(
            mesAno = mesAno,
            entries = entries.sortedBy { it.pago },
            incomes = incomes,
            totalRecebido = totalRecebido,
            totalContas = totalContas,
            totalPago = totalPago,
            variacaoPercentual = variacaoPercentual,
            breakdown = breakdown,
        )
    }

    fun goToMonth(mesAno: YearMonth) {
        selectedMonth.value = mesAno
    }

    fun toggleEntryPaid(entry: MonthBillEntryEntity) {
        viewModelScope.launch { repository.setEntryPaid(entry, !entry.pago) }
    }

    fun updateEntry(entry: MonthBillEntryEntity) {
        viewModelScope.launch { repository.updateMonthEntry(entry) }
    }

    fun deleteEntry(entry: MonthBillEntryEntity) {
        viewModelScope.launch { repository.deleteMonthEntry(entry) }
    }

    fun addExpense(
        nome: String,
        categoria: BillCategory,
        valorCentavos: Long,
        meses: List<YearMonth> = listOf(selectedMonth.value),
        iconKey: String? = null,
        photoPath: String? = null,
    ) {
        viewModelScope.launch {
            repository.addExpenseForMonths(meses, nome, categoria, valorCentavos, iconKey, photoPath)
        }
    }

    fun addIncome(descricao: String, valorCentavos: Long) {
        viewModelScope.launch {
            repository.addIncome(selectedMonth.value, descricao, valorCentavos)
        }
    }

    fun updateIncome(income: MonthlyIncomeEntity) {
        viewModelScope.launch { repository.updateIncome(income) }
    }

    fun deleteIncome(income: MonthlyIncomeEntity) {
        viewModelScope.launch { repository.deleteIncome(income) }
    }
}
