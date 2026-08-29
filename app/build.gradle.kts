plugins {
    alias(libs.plugins.gyurun.android.application.compose)
}

android {
    namespace = "com.gyugle.gyurun"

    flavorDimensions += "mapProvider"
    productFlavors {
        create("google") {
            dimension = "mapProvider"
        }
        create("kakao") {
            dimension = "mapProvider"
            ndk {
                // 카카오맵 요구사양....
                abiFilters += listOf("arm64-v8a", "armeabi-v7a")
            }
        }
    }
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))
    implementation(project(":core:connectivity:data"))
    implementation(project(":core:presentation:designsystem"))
    implementation(project(":core:presentation:ui"))
    implementation(project(":run:location"))
    implementation(project(":run:sensor"))

    implementation(project(":feature:onboarding:presentation:impl"))
    implementation(project(":feature:onboarding:presentation:api"))

    implementation(project(":feature:overview:presentation:api"))
    implementation(project(":feature:overview:presentation:impl"))

    implementation(project(":feature:active:presentation:impl"))
    implementation(project(":feature:details:presentation:impl"))
    implementation(project(":feature:stats:presentation:impl"))
    implementation(project(":feature:settings:presentation:impl"))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.workmanager)
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.compose.navigation3)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.work.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.androidx.lifecycle.viewmodel.navigation3)

    implementation(libs.timber)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)

    implementation(libs.kotlinx.coroutines.core)
}