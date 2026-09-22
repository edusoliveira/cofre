package io.edusoliveira.meudia.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.edusoliveira.meudia.data.FinanceRepository
import io.edusoliveira.meudia.data.db.BillCategory
import io.edusoliveira.meudia.data.db.BillType
import io.edusoliveira.meudia.data.db.entities.RecurringBillEntity
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RecurringBillsViewModel(private val repository: FinanceRepository) : ViewModel() {

    val bills: StateFlow<List<RecurringBillEntity>> = repository.observeRecurringBills()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addBill(
        nome: String,
        categoria: BillCategory,
        valorPadrao: Long,
        tipo: BillType,
        diaVencimento: Int,
        mesVencimento: Int?,
        iconKey: String? = null,
        photoPath: String? = null,
    ) {
        viewModelScope.launch {
            repository.addRecurringBill(nome, categoria, valorPadrao, tipo, diaVencimento, mesVencimento, iconKey, photoPath)
        }
    }

    fun updateBill(bill: RecurringBillEntity) {
        viewModelScope.launch { repository.updateRecurringBill(bill) }
    }

    fun setActive(bill: RecurringBillEntity, ativa: Boolean) {
        viewModelScope.launch { repository.setRecurringBillActive(bill.id, ativa) }
    }
}
