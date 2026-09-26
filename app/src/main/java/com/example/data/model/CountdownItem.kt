package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "countdowns")
data class CountdownItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val targetEpochMillis: Long,
    val targetDateDisplay: String,
    val targetTimeDisplay: String,
    val orderIndex: Int = 0,
    val isDefault: Boolean = false,
    val isCompleted: Boolean = false
)
