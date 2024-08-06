package com.yaabelozerov.moodb.data.room.mood

import com.yaabelozerov.moodb.data.model.DefaultMoodType
import kotlinx.coroutines.flow.Flow

interface RecordDao {
    fun getAll(): Flow<List<RecordEntity>>
    suspend fun insertRecord(record: RecordEntity)
    suspend fun updateType(recordId: Long, type: DefaultMoodType)
    suspend fun removeRecord(recordId: Long)
}

interface RecordDatabase<T> {
    fun dao(): T
}