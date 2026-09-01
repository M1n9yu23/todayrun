plugins {
    alias(libs.plugins.gyurun.android.library)
}

android {
    namespace = "com.gyugle.gyurun.run.sensor"
}

dependencies {
    implementation(project(":run:domain"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
}
