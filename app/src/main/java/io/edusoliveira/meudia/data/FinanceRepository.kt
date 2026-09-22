package io.edusoliveira.meudia.data

import io.edusoliveira.meudia.data.db.BillCategory
import io.edusoliveira.meudia.data.db.BillType
import io.edusoliveira.meudia.data.db.dao.MonthBillEntryDao
import io.edusoliveira.meudia.data.db.dao.MonthlyIncomeDao
import io.edusoliveira.meudia.data.db.dao.RecurringBillDao
import io.edusoliveira.meudia.data.db.entities.MonthBillEntryEntity
import io.edusoliveira.meudia.data.db.entities.MonthlyIncomeEntity
import io.edusoliveira.meudia.data.db.entities.RecurringBillEntity
import io.edusoliveira.meudia.util.toKey
import io.edusoliveira.meudia.util.toYearMonth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import java.time.YearMonth

class FinanceRepository(
    private val recurringBillDao: RecurringBillDao,
    private val monthBillEntryDao: MonthBillEntryDao,
    private val monthlyIncomeDao: MonthlyIncomeDao,
) {
    fun observeRecurringBills(): Flow<List<RecurringBillEntity>> = recurringBillDao.observeAll()

    suspend fun addRecurringBill(
        nome: String,
        categoria: BillCategory,
        valorPadrao: Long,
        tipo: BillType,
        diaVencimento: Int,
        mesVencimento: Int?,
        iconKey: String? = null,
        photoPath: String? = null,
    ): Long = recurringBillDao.insert(
        RecurringBillEntity(
            nome = nome,
            categoria = categoria,
            valorPadrao = valorPadrao,
            tipo = tipo,
            diaVencimento = diaVencimento,
            mesVencimento = mesVencimento,
            iconKey = iconKey,
            photoPath = photoPath,
        ),
    )

    suspend fun updateRecurringBill(bill: RecurringBillEntity) = recurringBillDao.update(bill)

    suspend fun setRecurringBillActive(id: Long, ativa: Boolean) =
        recurringBillDao.setActive(id, ativa)

    fun observeMonthEntries(mesAno: YearMonth): Flow<List<MonthBillEntryEntity>> =
        monthBillEntryDao.observeForMonth(mesAno.toKey())

    fun observeMonthIncomes(mesAno: YearMonth): Flow<List<MonthlyIncomeEntity>> =
        monthlyIncomeDao.observeForMonth(mesAno.toKey())

    /** Todos os meses que já têm alguma conta ou renda lançada, do mais recente ao mais antigo. */
    fun observeAllMonthsWithData(): Flow<List<YearMonth>> = combine(
        monthBillEntryDao.observeAllMonths(),
        monthlyIncomeDao.observeAllMonths(),
    ) { billMonths, incomeMonths ->
        (billMonths + incomeMonths)
            .distinct()
            .map { it.toYearMonth() }
            .sortedDescending()
    }

    suspend fun addIncome(mesAno: YearMonth, descricao: String, valor: Long) {
        monthlyIncomeDao.insert(
            MonthlyIncomeEntity(mesAno = mesAno.toKey(), descricao = descricao, valor = valor),
        )
    }

    suspend fun updateIncome(income: MonthlyIncomeEntity) = monthlyIncomeDao.update(income)

    suspend fun deleteIncome(income: MonthlyIncomeEntity) = monthlyIncomeDao.delete(income)

    suspend fun addAvulsoExpense(
        mesAno: YearMonth,
        nome: String,
        categoria: BillCategory,
        valor: Long,
        iconKey: String? = null,
        photoPath: String? = null,
    ) {
        monthBillEntryDao.insert(
            MonthBillEntryEntity(
                recurringBillId = null,
                mesAno = mesAno.toKey(),
                nome = nome,
                categoria = categoria,
                valor = valor,
                pago = false,
                iconKey = iconKey,
                photoPath = photoPath,
            ),
        )
    }

    /**
     * Lança a mesma conta avulsa em vários meses de uma vez (ex: uma dívida
     * parcelada em setembro e outubro). Cada mês recebe seu próprio
     * lançamento independente, já visível assim que o usuário navegar até lá.
     */
    suspend fun addExpenseForMonths(
        meses: List<YearMonth>,
        nome: String,
        categoria: BillCategory,
        valor: Long,
        iconKey: String? = null,
        photoPath: String? = null,
    ) {
        if (meses.isEmpty()) return
        monthBillEntryDao.insertAll(
            meses.map { mesAno ->
                MonthBillEntryEntity(
                    recurringBillId = null,
                    mesAno = mesAno.toKey(),
                    nome = nome,
                    categoria = categoria,
                    valor = valor,
                    pago = false,
                    iconKey = iconKey,
                    photoPath = photoPath,
                )
            },
        )
    }

    suspend fun updateMonthEntry(entry: MonthBillEntryEntity) = monthBillEntryDao.update(entry)

    suspend fun deleteMonthEntry(entry: MonthBillEntryEntity) = monthBillEntryDao.delete(entry)

    suspend fun setEntryPaid(entry: MonthBillEntryEntity, pago: Boolean) {
        monthBillEntryDao.update(
            entry.copy(
                pago = pago,
                dataPagamento = if (pago) System.currentTimeMillis() else null,
            ),
        )
    }

    /**
     * Garante que as contas recorrentes ativas apareçam como lançamentos do mês.
     * Mensais entram sempre; anuais só no mês configurado de vencimento.
     * Nunca duplica: só insere contas recorrentes que ainda não têm lançamento naquele mês.
     */
    suspend fun ensureMonthSeeded(mesAno: YearMonth) {
        val key = mesAno.toKey()
        val existing = monthBillEntryDao.getForMonthOnce(key)
        val existingRecurringIds = existing.mapNotNull { it.recurringBillId }.toSet()
        val activeBills = recurringBillDao.getActiveOnce()

        val toSeed = activeBills.filter { bill ->
            bill.id !in existingRecurringIds && when (bill.tipo) {
                BillType.MENSAL -> true
                BillType.ANUAL -> bill.mesVencimento == mesAno.monthValue
            }
        }

        if (toSeed.isNotEmpty()) {
            monthBillEntryDao.insertAll(
                toSeed.map { bill ->
                    MonthBillEntryEntity(
                        recurringBillId = bill.id,
                        mesAno = key,
                        nome = bill.nome,
                        categoria = bill.categoria,
                        valor = bill.valorPadrao,
                        pago = false,
                        iconKey = bill.iconKey,
                        photoPath = bill.photoPath,
                    )
                },
            )
        }
    }

    suspend fun getMonthSummaryOnce(mesAno: YearMonth): MonthSummary {
        val key = mesAno.toKey()
        val entries = monthBillEntryDao.getForMonthOnce(key)
        val incomes = monthlyIncomeDao.getForMonthOnce(key)
        return MonthSummary(
            mesAno = mesAno,
            totalContas = entries.sumOf { it.valor },
            totalPago = entries.filter { it.pago }.sumOf { it.valor },
            totalRecebido = incomes.sumOf { it.valor },
        )
    }
}
