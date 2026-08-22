plugins {
    alias(libs.plugins.gyurun.android.library.compose)
}

android {
    namespace = "com.gyugle.gyurun.core.presentation.designsystem"
}

dependencies {
    implementation(libs.coil.compose)
}