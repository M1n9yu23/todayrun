plugins {
    alias(libs.plugins.gyurun.android.library)
}

android {
    namespace = "com.gyugle.gyurun.core.navigation"
}

dependencies {
    api(libs.androidx.navigation3.runtime)
}
