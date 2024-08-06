package com.yaabelozerov.moodb.data.room.mood

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RecordEntity::class], version = 1, exportSchema = false)
abstract class MoodDatabase: RecordDatabase<MoodDao>, RoomDatabase() {
    abstract override fun dao(): MoodDao
}