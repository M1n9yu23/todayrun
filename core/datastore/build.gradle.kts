plugins {
    alias(libs.plugins.gyurun.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gyugle.gyurun.core.datastore"
}

dependencies {
    implementation(project(":core:domain"))
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
}
