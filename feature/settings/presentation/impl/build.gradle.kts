plugins {
    alias(libs.plugins.gyurun.android.feature)
}

android {
    namespace = "com.gyugle.gyurun.feature.settings.presentation.impl"

    defaultConfig {
        missingDimensionStrategy("mapProvider", "google")
    }
}

dependencies {
    implementation(project(":feature:settings:presentation:api"))
    implementation(project(":core:map"))
    implementation(project(":core:domain"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.browser)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.timber)
}
