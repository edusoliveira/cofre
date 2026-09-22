package io.edusoliveira.meudia.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "monthly_incomes", indices = [Index("mesAno")])
data class MonthlyIncomeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mesAno: String,
    val descricao: String,
    val valor: Long,
)
