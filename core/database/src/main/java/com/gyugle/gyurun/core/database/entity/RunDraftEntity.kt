package com.gyugle.gyurun.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

internal const val DRAFT_ID = 0

@Entity(tableName = "run_draft")
internal data class RunDraftEntity(
    @PrimaryKey
    val id: Int = DRAFT_ID,
    val dateTimeUtc: String,
    val durationMillis: Long,
    val distanceMeters: Int,
    val route: String?,
    val steps: Int?,
)