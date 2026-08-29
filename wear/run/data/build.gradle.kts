plugins {
    alias(libs.plugins.gyurun.android.library)
}

android {
    namespace = "com.gyugle.gyurun.wear.run.data"

    defaultConfig {
        minSdk =
            libs.versions.projectWearMinSdkVersion
                .get()
                .toInt()
    }
}

dependencies {
    implementation(project(":wear:run:domain"))
    implementation(project(":core:common"))
    implementation(project(":core:connectivity:domain"))

    implementation(libs.androidx.health.services.client)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.core.ktx)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    implementation(libs.timber)
}