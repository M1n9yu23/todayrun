package com.gyugle.gyurun.convention

import com.android.build.api.dsl.ApplicationExtension
import org.gradle.api.Project
import java.util.Properties

internal fun Project.configureAppBuildTypes(extension: ApplicationExtension) {
    val keystorePropertiesFile = rootProject.file("keystore.properties")
    val hasKeystore = keystorePropertiesFile.exists()

    extension.apply {
        if (hasKeystore) {
            val keystoreProperties = Properties().apply {
                keystorePropertiesFile.inputStream().use { load(it) }
            }
            signingConfigs {
                create("release") {
                    storeFile = rootProject.file(keystoreProperties.getProperty("storeFile"))
                    storePassword = keystoreProperties.getProperty("storePassword")
                    keyAlias = keystoreProperties.getProperty("keyAlias")
                    keyPassword = keystoreProperties.getProperty("keyPassword")
                }
            }
        }

        buildTypes {
            debug {
                applicationIdSuffix = ".debug"
                versionNameSuffix = "-debug"
            }
            release {
                if (hasKeystore) {
                    signingConfig = signingConfigs.getByName("release")
                }
            }
        }
    }
}