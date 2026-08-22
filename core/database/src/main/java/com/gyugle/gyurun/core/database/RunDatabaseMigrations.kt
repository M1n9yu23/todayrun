package com.gyugle.gyurun.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

internal val MIGRATION_1_2 =
    object : Migration(1, 2) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE run ADD COLUMN weatherType TEXT")
            db.execSQL("ALTER TABLE run ADD COLUMN weatherTemperatureCelsius REAL")
            db.execSQL("ALTER TABLE run ADD COLUMN weatherFeelsLikeCelsius REAL")
            db.execSQL("ALTER TABLE run ADD COLUMN weatherHumidityPercent INTEGER")
            db.execSQL("ALTER TABLE run ADD COLUMN weatherWindSpeedKmh REAL")
        }
    }

internal val MIGRATION_2_3 =
    object : Migration(2, 3) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE run ADD COLUMN route TEXT")
        }
    }

internal val MIGRATION_3_4 =
    object : Migration(3, 4) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL("ALTER TABLE run ADD COLUMN steps INTEGER")
        }
    }

internal val MIGRATION_4_5 =
    object : Migration(4, 5) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `run_draft` (" +
                        "`id` INTEGER NOT NULL, " +
                        "`dateTimeUtc` TEXT NOT NULL, " +
                        "`durationMillis` INTEGER NOT NULL, " +
                        "`distanceMeters` INTEGER NOT NULL, " +
                        "`route` TEXT, " +
                        "`steps` INTEGER, " +
                        "PRIMARY KEY(`id`))",
            )
        }
    }