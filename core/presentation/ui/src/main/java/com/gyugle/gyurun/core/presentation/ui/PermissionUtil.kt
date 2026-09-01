package com.gyugle.gyurun.core.presentation.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

fun Context.hasLocationPermission(): Boolean = hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)

fun ComponentActivity.shouldShowLocationPermissionRationale(): Boolean =
    shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)

fun Context.hasActivityRecognitionPermission(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        hasPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    } else {
        true
    }

fun ComponentActivity.shouldShowActivityRecognitionPermissionRationale(): Boolean =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        shouldShowRequestPermissionRationale(Manifest.permission.ACTIVITY_RECOGNITION)
    } else {
        false
    }

fun Context.hasNotificationPermission(): Boolean = hasPermission(Manifest.permission.POST_NOTIFICATIONS)

fun Context.openAppSettings() {
    val intent =
        Intent(
            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
            Uri.fromParts("package", packageName, null),
        )
    startActivity(intent)
}

private fun Context.hasPermission(permission: String): Boolean =
    ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
