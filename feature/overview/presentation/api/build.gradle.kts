plugins {
    alias(libs.plugins.gyurun.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gyugle.gyurun.feature.overview.presentation.api"
}

dependencies {
    api(project(":core:navigation"))
}
