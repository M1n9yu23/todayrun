package com.gyugle.gyurun.wear.app

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.gyugle.gyurun.core.common.shouldAskNotificationPermission
import com.gyugle.gyurun.wear.designsystem.theme.WearGyuRunTheme
import com.gyugle.gyurun.wear.run.presentation.TrackerScreenRoot
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val notificationPermissionStore: NotificationPermissionStore by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WearGyuRunTheme {
                TrackerScreenRoot()
                NotificationPermissionEffect(notificationPermissionStore)
            }
        }
    }
}

@Composable
private fun NotificationPermissionEffect(store: NotificationPermissionStore) {
    val context = LocalContext.current
    val hasAsked by store.hasAsked.collectAsState(initial = true)
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) { }

    LaunchedEffect(hasAsked) {
        if (shouldAskNotificationPermission(hasAsked, context.hasNotificationPermission())) {
            store.markAsked()
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private fun Context.hasNotificationPermission(): Boolean =
    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED
