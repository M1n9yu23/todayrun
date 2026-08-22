import com.android.build.api.dsl.ApplicationExtension
import com.gyugle.gyurun.convention.configureAppBuildTypes
import com.gyugle.gyurun.convention.configureKotlinAndroid
import com.gyugle.gyurun.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

class AndroidApplicationConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("com.android.application")

            extensions.configure<ApplicationExtension> {
                defaultConfig {
                    applicationId = libs.findVersion("projectApplicationId").get().toString()
                    targetSdk = libs.findVersion("projectTargetSdkVersion").get().toString().toInt()
                    versionCode = libs.findVersion("projectVersionCode").get().toString().toInt()
                    versionName = libs.findVersion("projectVersionName").get().toString()
                }

                buildFeatures {
                    buildConfig = true
                }

                configureKotlinAndroid(this)
                configureAppBuildTypes(this)
            }
        }
    }
}