plugins {
    alias(libs.plugins.gyurun.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.gyugle.gyurun.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "BASE_URL", "")
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.bundles.ktor)
    implementation(libs.timber)
}