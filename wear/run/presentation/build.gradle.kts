plugins {
    alias(libs.plugins.gyurun.android.library.compose)
}

android {
    namespace = "com.gyugle.gyurun.wear.run.presentation"

    defaultConfig {
        minSdk =
            libs.versions.projectWearMinSdkVersion
                .get()
                .toInt()
    }
}

dependencies {
    implementation(project(":wear:designsystem"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:presentation:ui"))
    implementation(project(":core:connectivity:domain"))
    implementation(project(":wear:run:domain"))

    implementation(libs.androidx.wear.compose.material3)
    implementation(libs.androidx.wear.compose.foundation)
    implementation(libs.androidx.wear.compose.ui.tooling)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)
}
