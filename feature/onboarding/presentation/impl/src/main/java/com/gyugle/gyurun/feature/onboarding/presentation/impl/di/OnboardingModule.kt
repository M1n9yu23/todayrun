package com.gyugle.gyurun.feature.onboarding.presentation.impl.di

import com.gyugle.gyurun.core.domain.preferences.repository.UserSettingsRepository
import com.gyugle.gyurun.feature.onboarding.presentation.api.OnboardingNavKey
import com.gyugle.gyurun.feature.onboarding.presentation.impl.OnboardingScreenRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.koin.dsl.module
import org.koin.dsl.navigation3.navigation

val onboardingPresentationModule =
    module {
        navigation<OnboardingNavKey> {
            val userSettingsRepository = get<UserSettingsRepository>()
            val applicationScope = get<CoroutineScope>()
            OnboardingScreenRoot(
                onFinishOnboarding = {
                    applicationScope.launch {
                        userSettingsRepository.setHasCompletedOnboarding(true)
                    }
                },
            )
        }
    }
