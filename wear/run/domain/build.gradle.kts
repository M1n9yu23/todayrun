plugins {
    alias(libs.plugins.gyurun.jvm.library)
}

dependencies {
    implementation(project(":core:common"))
    implementation(project(":core:connectivity:domain"))
    implementation(libs.kotlinx.coroutines.core)
}
