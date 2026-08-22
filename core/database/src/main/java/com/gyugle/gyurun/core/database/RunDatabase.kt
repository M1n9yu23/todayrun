package com.gyugle.gyurun.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.gyugle.gyurun.core.database.dao.RunDao
import com.gyugle.gyurun.core.database.dao.RunDraftDao
import com.gyugle.gyurun.core.database.entity.RunDraftEntity
import com.gyugle.gyurun.core.database.entity.RunEntity

@Database(
    entities = [RunEntity::class, RunDraftEntity::class],
    version = 5
)
internal abstract class RunDatabase : RoomDatabase() {
    abstract val runDao: RunDao

    abstract val runDraftDao: RunDraftDao
}