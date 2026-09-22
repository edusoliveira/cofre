package io.edusoliveira.meudia.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.edusoliveira.meudia.data.db.BillCategory
import io.edusoliveira.meudia.data.db.BillType

/**
 * Uma conta recorrente cadastrada uma vez (ex: TOTALPASS, TIM, Faculdade).
 * mesVencimento só é usado quando tipo == ANUAL, para saber em qual mês
 * do ano a conta deve ser sugerida automaticamente.
 */
@Entity(tableName = "recurring_bills")
data class RecurringBillEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nome: String,
    val categoria: BillCategory,
    val valorPadrao: Long,
    val tipo: BillType,
    val diaVencimento: Int,
    val mesVencimento: Int? = null,
    val ativa: Boolean = true,
    /** Chave de um ícone de [io.edusoliveira.meudia.ui.finance.IconLibrary], substitui o ícone padrão da categoria. */
    val iconKey: String? = null,
    /** Caminho de uma foto local (armazenamento interno do app), substitui o ícone. */
    val photoPath: String? = null,
)
