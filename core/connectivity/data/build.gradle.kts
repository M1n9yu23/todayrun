plugins {
    alias(libs.plugins.gyurun.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gyugle.gyurun.core.connectivity.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:connectivity:domain"))
    implementation(libs.google.android.gms.play.services.wearable)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.play.services)
    implementation(libs.kotlinx.serialization.json)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.timber)
}