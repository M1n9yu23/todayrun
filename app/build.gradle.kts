plugins {
    alias(libs.plugins.gyurun.android.application.compose)
}

android {
    namespace = "com.gyugle.gyurun"
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":core:datastore"))
    implementation(project(":core:data"))
    implementation(project(":core:navigation"))

    implementation(project(":feature:onboarding:presentation:impl"))

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)

    implementation(libs.koin.androidx.workmanager)
    implementation(libs.androidx.work.runtime)

    implementation(libs.timber)

    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.core.splashscreen)
}