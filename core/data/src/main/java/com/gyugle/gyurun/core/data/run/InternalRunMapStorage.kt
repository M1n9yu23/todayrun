package com.gyugle.gyurun.core.data.run

import com.gyugle.gyurun.core.common.DispatcherProvider
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.IOException
import java.util.UUID

internal class InternalRunMapStorage(
    private val filesDir: File,
    private val dispatchers: DispatcherProvider,
) : RunMapStorage {
    override suspend fun savePicture(bytes: ByteArray): String? =
        withContext(dispatchers.io) {
            val directory = File(filesDir, "run_maps").apply { mkdirs() }
            val file = File(directory, "run_${UUID.randomUUID()}.png")
            try {
                file.writeBytes(bytes)
                file.absolutePath
            } catch (e: IOException) {
                Timber.e(e, "러닝 지도 스냅샷 저장 실패")
                file.delete()
                null
            }
        }

    override suspend fun deletePicture(path: String?) {
        if (path == null) return
        withContext(dispatchers.io) {
            val file = File(path)
            if (file.exists() && !file.delete()) {
                Timber.w("러닝 지도 스냅샷 삭제 실패: %s", path)
            }
        }
    }
}
