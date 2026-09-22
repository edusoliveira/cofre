package io.edusoliveira.meudia

import android.app.Application
import io.edusoliveira.meudia.data.FinanceRepository
import io.edusoliveira.meudia.data.db.AppDatabase

class MeuDiaApp : Application() {

    lateinit var repository: FinanceRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val database = AppDatabase.getInstance(this)
        repository = FinanceRepository(
            recurringBillDao = database.recurringBillDao(),
            monthBillEntryDao = database.monthBillEntryDao(),
            monthlyIncomeDao = database.monthlyIncomeDao(),
        )
    }
}
