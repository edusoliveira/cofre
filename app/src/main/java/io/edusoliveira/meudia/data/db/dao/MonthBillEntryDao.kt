package io.edusoliveira.meudia.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.edusoliveira.meudia.data.db.entities.MonthBillEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MonthBillEntryDao {
    @Query("SELECT * FROM month_bill_entries WHERE mesAno = :mesAno ORDER BY pago ASC, valor DESC")
    fun observeForMonth(mesAno: String): Flow<List<MonthBillEntryEntity>>

    @Query("SELECT * FROM month_bill_entries WHERE mesAno = :mesAno")
    suspend fun getForMonthOnce(mesAno: String): List<MonthBillEntryEntity>

    @Query("SELECT DISTINCT mesAno FROM month_bill_entries")
    fun observeAllMonths(): Flow<List<String>>

    @Insert
    suspend fun insert(entry: MonthBillEntryEntity): Long

    @Insert
    suspend fun insertAll(entries: List<MonthBillEntryEntity>)

    @Update
    suspend fun update(entry: MonthBillEntryEntity)

    @Delete
    suspend fun delete(entry: MonthBillEntryEntity)
}
