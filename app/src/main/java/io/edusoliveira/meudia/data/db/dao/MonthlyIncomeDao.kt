package io.edusoliveira.meudia.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.edusoliveira.meudia.data.db.entities.MonthlyIncomeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthlyIncomeDao {
    @Query("SELECT * FROM monthly_incomes WHERE mesAno = :mesAno ORDER BY id ASC")
    fun observeForMonth(mesAno: String): Flow<List<MonthlyIncomeEntity>>

    @Query("SELECT * FROM monthly_incomes WHERE mesAno = :mesAno")
    suspend fun getForMonthOnce(mesAno: String): List<MonthlyIncomeEntity>

    @Query("SELECT DISTINCT mesAno FROM monthly_incomes")
    fun observeAllMonths(): Flow<List<String>>

    @Insert
    suspend fun insert(income: MonthlyIncomeEntity): Long

    @Update
    suspend fun update(income: MonthlyIncomeEntity)

    @Delete
    suspend fun delete(income: MonthlyIncomeEntity)
}
