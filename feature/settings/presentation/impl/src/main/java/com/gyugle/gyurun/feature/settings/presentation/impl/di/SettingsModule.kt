package com.gyugle.gyurun.feature.settings.presentation.impl.di

import com.gyugle.gyurun.core.navigation.Navigator
import com.gyugle.gyurun.feature.settings.presentation.api.AboutNavKey
import com.gyugle.gyurun.feature.settings.presentation.api.SettingsNavKey
import com.gyugle.gyurun.feature.settings.presentation.impl.AboutScreenRoot
import com.gyugle.gyurun.feature.settings.presentation.impl.AndroidAppVersionProvider
import com.gyugle.gyurun.feature.settings.presentation.impl.AppVersionProvider
import com.gyugle.gyurun.feature.settings.presentation.impl.SettingsScreenRoot
import com.gyugle.gyurun.feature.settings.presentation.impl.SettingsViewModel
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val settingsPresentationModule =
    module {
        singleOf(::AndroidAppVersionProvider) { bind<AppVersionProvider>() }
        viewModelOf(::SettingsViewModel)
        navigation<SettingsNavKey> {
            val navigator = get<Navigator>()
            SettingsScreenRoot(
                onBackClick = { navigator.navigateBack() },
                onOpenAbout = { navigator.navigateTo(AboutNavKey) }
            )
        }
        navigation<AboutNavKey> {
            val navigator = get<Navigator>()
            AboutScreenRoot(
                onBackClick = { navigator.navigateBack() }
            )
        }
    }