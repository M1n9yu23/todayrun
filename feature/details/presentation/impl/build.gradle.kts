plugins {
    alias(libs.plugins.gyurun.android.feature)
}

android {
    namespace = "com.gyugle.gyurun.feature.details.presentation.impl"

    defaultConfig {
        missingDimensionStrategy("mapProvider", "google")
    }
}

dependencies {
    implementation(project(":feature:details:presentation:api"))
    implementation(project(":core:domain"))
    implementation(project(":core:map"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.core)
}
