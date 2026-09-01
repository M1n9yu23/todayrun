package com.gyugle.gyurun.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gyugle.gyurun.core.navigation.Navigator
import com.gyugle.gyurun.feature.onboarding.presentation.api.OnboardingNavKey
import com.gyugle.gyurun.feature.overview.presentation.api.OverviewNavKey
import org.koin.compose.koinInject
import org.koin.compose.navigation3.koinEntryProvider

@Composable
fun GyuRunNavHost(
    hasCompletedOnboarding: Boolean,
    modifier: Modifier = Modifier,
) {
    if (hasCompletedOnboarding) {
        FeatureNavHost(startKey = OverviewNavKey, modifier = modifier)
    } else {
        FeatureNavHost(startKey = OnboardingNavKey, modifier = modifier)
    }
}

@Composable
private fun FeatureNavHost(
    startKey: NavKey,
    modifier: Modifier = Modifier,
) {
    val navigator = koinInject<Navigator>()
    val backStack = rememberNavBackStack(startKey)
    navigator.backStack = backStack

    NavDisplay(
        modifier = modifier,
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators =
            listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
            ),
        entryProvider = koinEntryProvider<NavKey>(),
    )
}
