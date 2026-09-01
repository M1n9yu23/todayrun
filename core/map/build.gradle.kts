import java.util.Properties

plugins {
    alias(libs.plugins.gyurun.android.library.compose)
}

val localProperties =
    Properties().apply {
        val localPropertiesFile = rootProject.file("local.properties")
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { load(it) }
        }
    }

val mapsApiKey: String = localProperties.getProperty("MAPS_API_KEY").orEmpty()
val kakaoAppKey: String = localProperties.getProperty("KAKAO_APP_KEY").orEmpty()

android {
    namespace = "com.gyugle.gyurun.core.map"

    buildFeatures {
        buildConfig = true
    }

    flavorDimensions += "mapProvider"
    productFlavors {
        create("google") {
            dimension = "mapProvider"
            manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        }
        create("kakao") {
            dimension = "mapProvider"
            buildConfigField("String", "KAKAO_APP_KEY", "\"$kakaoAppKey\"")
        }
    }
}

dependencies {
    implementation(project(":core:domain"))

    "googleImplementation"(libs.google.maps.android.compose)
    "googleImplementation"(libs.google.android.gms.play.services.maps)

    "kakaoImplementation"(libs.kakao.maps.android)
    "kakaoImplementation"(libs.androidx.lifecycle.runtime.compose)
    "kakaoImplementation"(libs.timber)
}
