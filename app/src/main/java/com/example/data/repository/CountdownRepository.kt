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

    suspend fun deduplicateAndEnsureDefaults(): List<CountdownItem> {
        val existing = dao.getAllCountdownsList()
        val dhakaZone = java.time.ZoneId.of("Asia/Dhaka")

        if (existing.isEmpty()) {
            val ssc27Target = java.time.ZonedDateTime.of(2027, 1, 7, 10, 0, 0, 0, dhakaZone).toInstant().toEpochMilli()
            val preTestTarget = java.time.ZonedDateTime.of(2026, 11, 15, 10, 0, 0, 0, dhakaZone).toInstant().toEpochMilli()
            val testExamTarget = java.time.ZonedDateTime.of(2026, 12, 10, 10, 0, 0, 0, dhakaZone).toInstant().toEpochMilli()

            val defaults = listOf(
                CountdownItem(
                    title = "SSC 27",
                    targetEpochMillis = ssc27Target,
                    targetDateDisplay = "7 January 2027",
                    targetTimeDisplay = "10:00 AM (BST)",
                    orderIndex = 0,
                    isDefault = true,
                    isCompleted = false
                ),
                CountdownItem(
                    title = "SSC Test Examination",
                    targetEpochMillis = testExamTarget,
                    targetDateDisplay = "10 December 2026",
                    targetTimeDisplay = "10:00 AM (BST)",
                    orderIndex = 1,
                    isDefault = false,
                    isCompleted = false
                ),
                CountdownItem(
                    title = "SSC Pre-Test Revision",
                    targetEpochMillis = preTestTarget,
                    targetDateDisplay = "15 November 2026",
                    targetTimeDisplay = "10:00 AM (BST)",
                    orderIndex = 2,
                    isDefault = false,
                    isCompleted = false
                )
            )
            dao.insertAll(defaults)
            return dao.getAllCountdownsList()
        }

        // Deduplication: group by normalized title
        val seenKeys = mutableSetOf<String>()
        val toKeep = mutableListOf<CountdownItem>()
        val toDelete = mutableListOf<CountdownItem>()

        for (item in existing) {
            val key = item.title.trim().lowercase()
            if (seenKeys.add(key)) {
                toKeep.add(item)
            } else {
                toDelete.add(item)
            }
        }

        for (duplicate in toDelete) {
            dao.delete(duplicate)
        }

        val reindexed = toKeep.mapIndexed { index, item ->
            item.copy(orderIndex = index)
        }
        dao.insertAll(reindexed)
        return reindexed
    }
}
