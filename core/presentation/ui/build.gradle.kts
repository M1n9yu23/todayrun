plugins {
    alias(libs.plugins.gyurun.android.library.compose)
}

android {
    namespace = "com.gyugle.gyurun.core.presentation.ui"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(libs.androidx.core.ktx)
    api(libs.androidx.activity)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.kotlinx.coroutines.core)
}