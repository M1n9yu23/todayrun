package com.gyugle.gyurun.wear.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.gyugle.gyurun.connectivity.data.di.connectivityModule
import com.gyugle.gyurun.core.common.di.coreCommonModule
import com.gyugle.gyurun.wear.app.NotificationPermissionStore
import com.gyugle.gyurun.wear.app.WearPreferences
import com.gyugle.gyurun.wear.app.WearPreferencesSerializer
import com.gyugle.gyurun.wear.run.data.di.wearRunDataModule
import com.gyugle.gyurun.wear.run.presentation.di.wearRunPresentationModule
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

internal val wearAppModule =
    module {
        single<DataStore<WearPreferences>> {
            DataStoreFactory.create(
                serializer = WearPreferencesSerializer,
                produceFile = { androidApplication().dataStoreFile("wear_prefs.json") },
            )
        }
        singleOf(::NotificationPermissionStore)
    }

val wearAppModules: List<Module> =
    listOf(
        coreCommonModule,
        connectivityModule,
        wearRunDataModule,
        wearRunPresentationModule,
        wearAppModule,
    )