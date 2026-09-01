import java.util.Properties

plugins {
    alias(libs.plugins.gyurun.android.library)
    alias(libs.plugins.kotlin.serialization)
}

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { load(it) }
        }
    }

val baseUrl: String = localProperties.getProperty("BASE_URL").orEmpty().trim()

android {
    namespace = "com.gyugle.gyurun.core.network"

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
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
