plugins {
    alias(libs.plugins.gyurun.android.feature)
}

android {
    namespace = "com.gyugle.gyurun.feature.stats.presentation.impl"
}

dependencies {
    implementation(project(":feature:stats:presentation:api"))
    implementation(project(":core:domain"))
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.compose.material3.adaptive)
}
