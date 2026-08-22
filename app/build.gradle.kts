plugins {
    alias(libs.plugins.gyurun.android.application.compose)
}

android {
    namespace = "com.gyugle.gyurun"
}

dependencies {
    implementation(libs.androidx.activity.compose)
}