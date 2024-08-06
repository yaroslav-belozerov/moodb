package com.yaabelozerov.moodb.data.room.mood

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yaabelozerov.moodb.data.model.DefaultMoodType

@Entity
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val recordId: Long,
    val timestamp: Long,
    val type: DefaultMoodType,
    val memo: String
)