plugins {
    alias(libs.plugins.gyurun.android.feature)
}

android {
    namespace = "com.gyugle.gyurun.feature.active.presentation.impl"

    defaultConfig {
        missingDimensionStrategy("mapProvider", "google")
    }
}

dependencies {
    implementation(project(":feature:active:presentation:api"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:connectivity:domain"))
    implementation(project(":core:map"))
    implementation(project(":run:domain"))
    implementation(project(":run:location"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.core)

}