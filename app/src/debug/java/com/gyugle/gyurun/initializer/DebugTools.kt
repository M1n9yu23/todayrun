package com.gyugle.gyurun.initializer

import android.app.Application
import android.os.StrictMode
import timber.log.Timber

fun Application.initializeDebugTools() {
    Timber.plant(Timber.DebugTree())

    StrictMode.setThreadPolicy(
        StrictMode.ThreadPolicy
            .Builder()
            .detectDiskReads()
            .detectDiskWrites()
            .detectNetwork()
            .penaltyLog()
            .build(),
    )
    StrictMode.setVmPolicy(
        StrictMode.VmPolicy
            .Builder()
            .detectLeakedClosableObjects()
            .detectLeakedSqlLiteObjects()
            .detectLeakedRegistrationObjects()
            .detectActivityLeaks()
            .penaltyLog()
            .build(),
    )
}