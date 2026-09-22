package io.edusoliveira.meudia.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import io.edusoliveira.meudia.data.FinanceRepository
import io.edusoliveira.meudia.ui.finance.FinanceViewModel
import io.edusoliveira.meudia.ui.finance.HistoryViewModel
import io.edusoliveira.meudia.ui.finance.RecurringBillsViewModel

class ViewModelFactory(private val repository: FinanceRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        FinanceViewModel::class.java -> FinanceViewModel(repository) as T
        RecurringBillsViewModel::class.java -> RecurringBillsViewModel(repository) as T
        HistoryViewModel::class.java -> HistoryViewModel(repository) as T
        else -> throw IllegalArgumentException("ViewModel desconhecida: ${modelClass.name}")
    }
}
