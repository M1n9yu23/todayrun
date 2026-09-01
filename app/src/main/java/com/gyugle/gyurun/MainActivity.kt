package com.gyugle.gyurun

import android.Manifest
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.gyugle.gyurun.core.common.shouldAskNotificationPermission
import com.gyugle.gyurun.core.domain.preferences.ThemeMode
import com.gyugle.gyurun.core.presentation.designsystem.GyuRunTheme
import com.gyugle.gyurun.core.presentation.ui.LocalDistanceUnit
import com.gyugle.gyurun.core.presentation.ui.hasNotificationPermission
import com.gyugle.gyurun.navigation.GyuRunNavHost
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel by viewModel<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen().setKeepOnScreenCondition {
            viewModel.state.isCheckingOnboarding
        }
        super.onCreate(savedInstanceState)
        (application as GyuRunApp)
            .useLanguageForWidget(resources.configuration.locales.toLanguageTags())
        enableEdgeToEdge()
        setContent {
            val state = viewModel.state
            val darkTheme =
                when (state.themeMode) {
                    ThemeMode.SYSTEM -> isSystemInDarkTheme()
                    ThemeMode.LIGHT -> false
                    ThemeMode.DARK -> true
                }

            GyuRunTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(LocalDistanceUnit provides state.distanceUnit) {
                    if (!state.isCheckingOnboarding) {
                        GyuRunNavHost(
                            hasCompletedOnboarding = state.hasCompletedOnboarding,
                            modifier = Modifier.fillMaxSize(),
                        )
                        if (state.hasCompletedOnboarding) {
                            NotificationPermissionEffect(
                                hasRequested = state.hasRequestedNotificationPermission,
                                onRequested = viewModel::onNotificationPermissionRequested,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationPermissionEffect(
    hasRequested: Boolean,
    onRequested: () -> Unit,
) {
    val context = LocalContext.current
    val permissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestPermission(),
        ) {
        }

    LaunchedEffect(hasRequested) {
        if (shouldAskNotificationPermission(hasRequested, context.hasNotificationPermission())) {
            onRequested()
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}
