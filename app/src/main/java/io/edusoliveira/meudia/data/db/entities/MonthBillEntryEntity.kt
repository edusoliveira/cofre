package io.edusoliveira.meudia.data.db.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import io.edusoliveira.meudia.data.db.BillCategory

/**
 * Ocorrência de uma conta em um mês específico. recurringBillId é nulo para
 * lançamentos avulsos (que não vêm de uma conta recorrente cadastrada).
 * nome e categoria são um snapshot: renomear a RecurringBillEntity depois
 * não deve alterar o histórico já lançado.
 */
@Entity(
    tableName = "month_bill_entries",
    foreignKeys = [
        ForeignKey(
            entity = RecurringBillEntity::class,
            parentColumns = ["id"],
            childColumns = ["recurringBillId"],
            onDelete = ForeignKey.SET_NULL,
        ),
    ],
    indices = [Index("recurringBillId"), Index("mesAno")],
)
data class MonthBillEntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val recurringBillId: Long? = null,
    val mesAno: String,
    val nome: String,
    val valor: Long,
    val categoria: BillCategory,
    val pago: Boolean = false,
    val dataPagamento: Long? = null,
    /** Snapshot do ícone/foto customizados da conta recorrente, se houver. */
    val iconKey: String? = null,
    val photoPath: String? = null,
)
