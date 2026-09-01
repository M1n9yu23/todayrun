plugins {
    alias(libs.plugins.gyurun.android.library)
}

android {
    namespace = "com.gyugle.gyurun.run.location"
}

dependencies {
    implementation(project(":run:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:connectivity:domain"))
    implementation(libs.google.android.gms.play.services.location)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.timber)
}
