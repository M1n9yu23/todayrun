plugins {
    alias(libs.plugins.gyurun.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gyugle.gyurun.feature.onboarding.presentation.api"
}

dependencies {
    api(project(":core:navigation"))
}
