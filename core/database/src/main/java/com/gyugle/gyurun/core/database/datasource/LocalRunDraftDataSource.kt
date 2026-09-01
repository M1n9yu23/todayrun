package com.gyugle.gyurun.core.database.datasource

import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.domain.run.RunDraft

interface LocalRunDraftDataSource {
    suspend fun getDraft(): RunDraft?

    suspend fun upsertDraft(draft: RunDraft): EmptyResult<DataError.Local>

    suspend fun deleteDraft()
}
