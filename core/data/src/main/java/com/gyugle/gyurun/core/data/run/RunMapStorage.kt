package com.gyugle.gyurun.core.data.run

internal interface RunMapStorage {
    suspend fun savePicture(bytes: ByteArray): String?

    suspend fun deletePicture(path: String?)
}
