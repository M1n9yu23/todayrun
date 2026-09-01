package com.gyugle.gyurun.core.data.run

import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.database.datasource.LocalRunDraftDataSource
import com.gyugle.gyurun.core.domain.run.RunDraft
import com.gyugle.gyurun.core.domain.run.repository.RunDraftRepository

internal class DefaultRunDraftRepository(
    private val localRunDraftDataSource: LocalRunDraftDataSource,
) : RunDraftRepository {
    override suspend fun getDraft(): RunDraft? = localRunDraftDataSource.getDraft()

    override suspend fun upsertDraft(draft: RunDraft): EmptyResult<DataError.Local> = localRunDraftDataSource.upsertDraft(draft)

    override suspend fun deleteDraft() {
        localRunDraftDataSource.deleteDraft()
    }
}
