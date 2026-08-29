import com.gyugle.gyurun.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidApplicationWearComposeConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("gyurun.android.application.compose")

            dependencies {
                "implementation"(libs.findLibrary("androidx.wear.compose.material3").get())
                "implementation"(libs.findLibrary("androidx.wear.compose.foundation").get())
                "implementation"(libs.findLibrary("androidx.wear.compose.ui.tooling").get())
                "implementation"(libs.findLibrary("google.android.gms.play.services.wearable").get())
            }
        }
    }
}