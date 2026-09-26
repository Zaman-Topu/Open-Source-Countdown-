package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.CountdownDao
import com.example.data.model.CountdownItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime

@Database(entities = [CountdownItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun countdownDao(): CountdownDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ssc27_countdown_database"
                )
                    .addCallback(DatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateInitialData(database.countdownDao())
                    }
                }
            }

            private suspend fun populateInitialData(dao: CountdownDao) {
                val dhakaZone = ZoneId.of("Asia/Dhaka")

                // Official SSC 2027 Target: 7 January 2027 at 10:00 AM Bangladesh Standard Time
                val ssc27Target = ZonedDateTime.of(
                    2027, 1, 7, 10, 0, 0, 0, dhakaZone
                ).toInstant().toEpochMilli()

                // Milestone 1: SSC Pre-Test / Model Exam
                val preTestTarget = ZonedDateTime.of(
                    2026, 11, 15, 10, 0, 0, 0, dhakaZone
                ).toInstant().toEpochMilli()

                // Milestone 2: SSC Test Exam
                val testExamTarget = ZonedDateTime.of(
                    2026, 12, 10, 10, 0, 0, 0, dhakaZone
                ).toInstant().toEpochMilli()

                val initialItems = listOf(
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

                dao.insertAll(initialItems)
            }
        }
    }
}
