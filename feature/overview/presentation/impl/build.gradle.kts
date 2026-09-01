plugins {
    alias(libs.plugins.gyurun.android.feature)
}

android {
    namespace = "com.gyugle.gyurun.feature.overview.presentation.impl"
}

dependencies {
    implementation(project(":feature:overview:presentation:api"))
    implementation(project(":feature:active:presentation:api"))
    implementation(project(":feature:details:presentation:api"))
    implementation(project(":feature:stats:presentation:api"))
    implementation(project(":feature:settings:presentation:api"))
    implementation(project(":core:common"))
    implementation(project(":core:domain"))
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.androidx.paging.compose)
    implementation(libs.androidx.compose.material3.adaptive)
}
