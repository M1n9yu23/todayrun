package com.gyugle.gyurun.core.domain.run.repository

import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.domain.run.RunDraft

/**
 * 달리는 중인 러닝을 위한 리포.
 */
interface RunDraftRepository {
    suspend fun getDraft(): RunDraft?

    suspend fun upsertDraft(draft: RunDraft): EmptyResult<DataError.Local>

    suspend fun deleteDraft()
}