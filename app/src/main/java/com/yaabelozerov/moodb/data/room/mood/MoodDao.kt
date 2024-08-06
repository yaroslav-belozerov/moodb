package com.yaabelozerov.moodb.data.room.mood

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.yaabelozerov.moodb.data.model.DefaultMoodType
import kotlinx.coroutines.flow.Flow

@Dao
interface MoodDao: RecordDao {
    @Query("SELECT * FROM recordentity ORDER BY timestamp ASC")
    override fun getAll(): Flow<List<RecordEntity>>

    @Insert
    override suspend fun insertRecord(record: RecordEntity)

    @Query("UPDATE recordentity SET type = :type WHERE recordId = :recordId")
    override suspend fun updateType(recordId: Long, type: DefaultMoodType)

    @Query("DELETE FROM recordentity WHERE recordId = :recordId")
    override suspend fun removeRecord(recordId: Long)
}