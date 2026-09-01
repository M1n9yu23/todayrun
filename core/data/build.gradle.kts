plugins {
    alias(libs.plugins.gyurun.android.library)
}

android {
    namespace = "com.gyugle.gyurun.core.data"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:database"))
    implementation(project(":core:network"))
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.paging.common)
    implementation(libs.androidx.work.runtime)
    implementation(libs.timber)
}
