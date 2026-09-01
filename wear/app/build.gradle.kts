plugins {
    alias(libs.plugins.gyurun.android.application.wear.compose)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gyugle.gyurun.wear.app"

    defaultConfig {
        minSdk =
            libs.versions.projectWearMinSdkVersion
                .get()
                .toInt()
    }
}

dependencies {
    implementation(project(":wear:designsystem"))
    implementation(project(":wear:run:presentation"))
    implementation(project(":wear:run:domain"))
    implementation(project(":wear:run:data"))
    implementation(project(":core:common"))
    implementation(project(":core:connectivity:data"))

    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.wear.tiles)
    implementation(libs.androidx.wear.protolayout)
    implementation(libs.androidx.wear.protolayout.material3)
    implementation(libs.androidx.concurrent.futures)
    implementation(libs.androidx.wear.ongoing)
    implementation(libs.androidx.core.ktx)

    implementation(libs.androidx.datastore)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.timber)
}
