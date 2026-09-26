package com.example.data.repository

import com.example.data.dao.CountdownDao
import com.example.data.model.CountdownItem
import kotlinx.coroutines.flow.Flow

class CountdownRepository(private val dao: CountdownDao) {

    val allCountdowns: Flow<List<CountdownItem>> = dao.getAllCountdowns()

    suspend fun getCount(): Int = dao.getCount()

    suspend fun insert(item: CountdownItem): Long = dao.insert(item)

    suspend fun update(item: CountdownItem) = dao.update(item)

    suspend fun delete(item: CountdownItem) = dao.delete(item)

    suspend fun deleteById(id: Long) = dao.deleteById(id)

    suspend fun reorder(items: List<CountdownItem>) {
        val updated = items.mapIndexed { index, item ->
            item.copy(orderIndex = index)
        }
        dao.insertAll(updated)
    }
}
