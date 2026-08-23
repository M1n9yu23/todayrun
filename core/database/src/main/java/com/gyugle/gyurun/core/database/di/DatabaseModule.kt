package com.gyugle.gyurun.core.database.di

import androidx.room.Room
import com.gyugle.gyurun.core.database.MIGRATION_1_2
import com.gyugle.gyurun.core.database.MIGRATION_2_3
import com.gyugle.gyurun.core.database.MIGRATION_3_4
import com.gyugle.gyurun.core.database.MIGRATION_4_5
import com.gyugle.gyurun.core.database.RunDatabase
import com.gyugle.gyurun.core.database.datasource.LocalRunDataSource
import com.gyugle.gyurun.core.database.datasource.LocalRunDraftDataSource
import com.gyugle.gyurun.core.database.datasource.impl.RoomLocalRunDataSource
import com.gyugle.gyurun.core.database.datasource.impl.RoomLocalRunDraftDataSource
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val databaseModule =
    module {
        single {
            Room
                .databaseBuilder(
                    androidApplication(),
                    RunDatabase::class.java,
                    "run.db"
                ).addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                .build()
        }
        single { get<RunDatabase>().runDao }
        single { get<RunDatabase>().runDraftDao }
        single<LocalRunDataSource> { RoomLocalRunDataSource(get()) }
        single<LocalRunDraftDataSource> { RoomLocalRunDraftDataSource(get()) }
    }