package io.edusoliveira.meudia.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.edusoliveira.meudia.data.db.entities.RecurringBillEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RecurringBillDao {
    @Query("SELECT * FROM recurring_bills ORDER BY nome ASC")
    fun observeAll(): Flow<List<RecurringBillEntity>>

    @Query("SELECT * FROM recurring_bills WHERE ativa = 1")
    suspend fun getActiveOnce(): List<RecurringBillEntity>

    @Insert
    suspend fun insert(bill: RecurringBillEntity): Long

    @Update
    suspend fun update(bill: RecurringBillEntity)

    @Query("UPDATE recurring_bills SET ativa = :ativa WHERE id = :id")
    suspend fun setActive(id: Long, ativa: Boolean)
}
