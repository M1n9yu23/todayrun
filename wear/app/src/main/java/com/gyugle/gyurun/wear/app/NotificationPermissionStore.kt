package com.gyugle.gyurun.wear.app

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class NotificationPermissionStore(
    private val dataStore: DataStore<WearPreferences>,
) {
    val hasAsked: Flow<Boolean> = dataStore.data.map { it.hasAskedNotificationPermission }

    suspend fun markAsked() {
        dataStore.updateData { it.copy(hasAskedNotificationPermission = true) }
    }
}
