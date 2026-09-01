plugins {
    alias(libs.plugins.gyurun.jvm.library)
}

dependencies {
    implementation(project(":core:common"))
    implementation(libs.kotlinx.coroutines.core)
}
