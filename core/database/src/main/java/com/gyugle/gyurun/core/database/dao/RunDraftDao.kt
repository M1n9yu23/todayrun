package com.gyugle.gyurun.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.gyugle.gyurun.core.database.entity.DRAFT_ID
import com.gyugle.gyurun.core.database.entity.RunDraftEntity

@Dao
internal interface RunDraftDao {
    @Upsert
    suspend fun upsertDraft(draft: RunDraftEntity)

    @Query("SELECT * FROM run_draft WHERE id = :id")
    suspend fun getDraft(id: Int = DRAFT_ID): RunDraftEntity?

    @Query("DELETE FROM run_draft")
    suspend fun deleteDraft()
}
