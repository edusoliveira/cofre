package io.edusoliveira.meudia.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.edusoliveira.meudia.data.FinanceRepository
import io.edusoliveira.meudia.data.MonthSummary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HistoryViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _summaries = MutableStateFlow<List<MonthSummary>>(emptyList())
    val summaries: StateFlow<List<MonthSummary>> = _summaries.asStateFlow()

    init {
        viewModelScope.launch {
            repository.observeAllMonthsWithData().collectLatest { meses ->
                _summaries.value = meses.map { repository.getMonthSummaryOnce(it) }
            }
        }
    }
}
