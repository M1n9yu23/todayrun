plugins {
    alias(libs.plugins.gyurun.jvm.library)
}

dependencies {
    implementation(libs.koin.core)
    implementation(platform(libs.koin.bom))
    implementation(libs.kotlinx.coroutines.core)
}