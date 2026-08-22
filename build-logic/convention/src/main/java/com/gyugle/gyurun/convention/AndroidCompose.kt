package com.gyugle.gyurun.convention

import com.android.build.api.dsl.CommonExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension

internal fun Project.configureAndroidCompose(
    commonExtension: CommonExtension
) {
    commonExtension.buildFeatures.apply {
        compose = true
    }

    dependencies {
        val bom = libs.findLibrary("androidx.compose.bom").get()
        "implementation"(platform(bom))
        "androidTestImplementation"(platform(bom))
        "implementation"(libs.findBundle("compose").get())
        "debugImplementation"(libs.findLibrary("androidx.compose.ui.tooling").get())
        "debugImplementation"(libs.findLibrary("androidx.compose.ui.test.manifest").get())
        "androidTestImplementation"(libs.findLibrary("androidx.compose.ui.test.junit4").get())
    }

    extensions.configure<ComposeCompilerGradlePluginExtension> {
        stabilityConfigurationFiles.add(
            rootProject.layout.projectDirectory.file("compose_compiler_config.conf")
        )

        if (providers.gradleProperty("gyurun.enableComposeCompilerReports").orNull.toBoolean()) {
            val reportsDir = layout.buildDirectory.dir("compose_compiler")
            reportsDestination.set(reportsDir)
            metricsDestination.set(reportsDir)
        }
    }
}