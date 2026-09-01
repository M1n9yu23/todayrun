plugins {
    alias(libs.plugins.gyurun.android.feature)
}

android {
    namespace = "com.gyugle.gyurun.feature.onboarding.presentation.impl"
}

dependencies {
    implementation(project(":feature:onboarding:presentation:api"))
    implementation(project(":core:domain"))
    implementation(libs.androidx.activity.compose)
    implementation(libs.kotlinx.coroutines.core)
}
