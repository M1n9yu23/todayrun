package com.gyugle.gyurun.feature.settings.presentation.impl

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build

internal interface AppVersionProvider {
    val versionName: String
    val versionCode: Long
}

internal class AndroidAppVersionProvider(
    context: Context,
) : AppVersionProvider {
    private val packageInfo =
        context.packageManager.packageInfoCompat(context.packageName)

    override val versionName: String = packageInfo.versionName.orEmpty()

    override val versionCode: Long =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            packageInfo.longVersionCode
        } else {
            @Suppress("DEPRECATION")
            packageInfo.versionCode.toLong()
        }
}

private fun PackageManager.packageInfoCompat(packageName: String): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        getPackageInfo(packageName, 0)
    }
