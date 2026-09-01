package com.gyugle.gyurun.wear.app

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

internal object WearPreferencesSerializer : Serializer<WearPreferences> {
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    override val defaultValue: WearPreferences = WearPreferences()

    override suspend fun readFrom(input: InputStream): WearPreferences =
        try {
            json.decodeFromString(
                deserializer = WearPreferences.serializer(),
                string = input.readBytes().decodeToString(),
            )
        } catch (e: SerializationException) {
            throw CorruptionException("wear_prefs 를 읽을 수 없습니다", e)
        }

    override suspend fun writeTo(
        t: WearPreferences,
        output: OutputStream,
    ) {
        output.write(
            json.encodeToString(WearPreferences.serializer(), t).encodeToByteArray(),
        )
    }
}
