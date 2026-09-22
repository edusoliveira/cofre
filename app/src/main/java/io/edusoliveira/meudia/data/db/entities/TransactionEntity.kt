package io.edusoliveira.meudia.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Registro simples de transação usado pela primeira versão do banco.
 * Mantido para não perder dados de instalações anteriores; o controle
 * financeiro completo vive em RecurringBillEntity/MonthBillEntryEntity/MonthlyIncomeEntity.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val titulo: String,
    val valor: Long,
    val data: Long,
    val entrada: Boolean,
)
