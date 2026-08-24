package com.gyugle.gyurun.core.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.gyugle.gyurun.core.datastore.DataStoreUserSettingsRepository
import com.gyugle.gyurun.core.datastore.UserPreferences
import com.gyugle.gyurun.core.datastore.UserPreferencesSerializer
import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataStoreModule =
    module {
        single<DataStore<UserPreferences>> {
            DataStoreFactory.create(
                serializer = UserPreferencesSerializer,
                produceFile = { androidApplication().dataStoreFile("user_prefs.json") }
            )
        }
        single<UserSettingsRepository> { DataStoreUserSettingsRepository(get()) }
    }