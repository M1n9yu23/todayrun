package com.gyugle.gyurun.core.database.datasource.impl

import android.database.sqlite.SQLiteFullException
import com.gyugle.gyurun.core.common.DataError
import com.gyugle.gyurun.core.common.EmptyResult
import com.gyugle.gyurun.core.common.Result
import com.gyugle.gyurun.core.database.dao.RunDraftDao
import com.gyugle.gyurun.core.database.datasource.LocalRunDraftDataSource
import com.gyugle.gyurun.core.database.mapper.toRunDraft
import com.gyugle.gyurun.core.database.mapper.toRunDraftEntity
import com.gyugle.gyurun.core.domain.run.RunDraft

internal class RoomLocalRunDraftDataSource(
    private val runDraftDao: RunDraftDao
) : LocalRunDraftDataSource {
    override suspend fun getDraft(): RunDraft? = runDraftDao.getDraft()?.toRunDraft()

    override suspend fun upsertDraft(draft: RunDraft): EmptyResult<DataError.Local> =
        try {
            runDraftDao.upsertDraft(draft.toRunDraftEntity())
            Result.Success(Unit)
        } catch (e: SQLiteFullException) {
            Result.Error(DataError.Local.DISK_FULL)
        }

    override suspend fun deleteDraft() {
        runDraftDao.deleteDraft()
    }
}