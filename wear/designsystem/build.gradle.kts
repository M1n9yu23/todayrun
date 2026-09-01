plugins {
    alias(libs.plugins.gyurun.android.library.compose)
}

android {
    namespace = "com.gyugle.gyurun.wear.designsystem"

    defaultConfig {
        minSdk =
            libs.versions.projectWearMinSdkVersion
                .get()
                .toInt()
    }
}

dependencies {
    api(project(":core:presentation:designsystem"))

    implementation(libs.androidx.wear.compose.material3)
    implementation(libs.androidx.wear.compose.foundation)
}
