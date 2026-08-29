package com.gyugle.gyurun.connectivity.data.messaging

import com.gyugle.gyurun.core.connectivity.domain.messaging.MessagingAction
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import timber.log.Timber

internal val messagingJson =
    Json {
        ignoreUnknownKeys = true
    }

internal fun encodeMessagingAction(action: MessagingAction): String =
    messagingJson.encodeToString(action.toMessagingActionDto())

internal fun decodeMessagingAction(json: String): MessagingAction? =
    try {
        messagingJson.decodeFromString<MessagingActionDto>(json).toMessagingAction()
    } catch (e: SerializationException) {
        Timber.w(e, "알아들을 수 없는 말이 왔다")
        null
    }