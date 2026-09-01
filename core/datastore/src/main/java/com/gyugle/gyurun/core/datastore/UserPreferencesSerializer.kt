package com.gyugle.gyurun.core.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

internal object UserPreferencesSerializer : Serializer<UserPreferences> {
    private val json =
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }

    override val defaultValue: UserPreferences = UserPreferences()

    override suspend fun readFrom(input: InputStream): UserPreferences =
        try {
            json.decodeFromString(
                deserializer = UserPreferences.serializer(),
                string = input.readBytes().decodeToString(),
            )
        } catch (e: SerializationException) {
            throw CorruptionException("user_prefs 를 읽을 수 없습니다", e)
        }

    override suspend fun writeTo(
        t: UserPreferences,
        output: OutputStream,
    ) {
        output.write(
            json.encodeToString(UserPreferences.serializer(), t).encodeToByteArray(),
        )
    }
}
