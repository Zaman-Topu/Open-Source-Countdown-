package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CountdownItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CountdownDao {
    @Query("SELECT * FROM countdowns ORDER BY orderIndex ASC, id ASC")
    fun getAllCountdowns(): Flow<List<CountdownItem>>

    @Query("SELECT * FROM countdowns ORDER BY orderIndex ASC, id ASC")
    suspend fun getAllCountdownsList(): List<CountdownItem>

    @Query("SELECT * FROM countdowns WHERE id = :id LIMIT 1")
    suspend fun getCountdownById(id: Long): CountdownItem?

    @Query("SELECT COUNT(*) FROM countdowns")
    suspend fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CountdownItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CountdownItem>)

    @Update
    suspend fun update(item: CountdownItem)

    @Delete
    suspend fun delete(item: CountdownItem)

    @Query("DELETE FROM countdowns WHERE id = :id")
    suspend fun deleteById(id: Long)
}
