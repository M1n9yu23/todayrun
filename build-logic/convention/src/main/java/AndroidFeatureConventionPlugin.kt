import com.gyugle.gyurun.convention.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidFeatureConventionPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.run {
            pluginManager.apply("gyurun.android.library.compose")

            dependencies {
                "implementation"(project(":core:navigation"))
                "implementation"(project(":core:presentation:designsystem"))
                "implementation"(project(":core:presentation:ui"))
                "implementation"(libs.findLibrary("androidx.lifecycle.viewmodel.ktx").get())
                "implementation"(platform(libs.findLibrary("koin.bom").get()))
                "implementation"(libs.findLibrary("koin.androidx.compose").get())
                "implementation"(libs.findLibrary("koin.compose.navigation3").get())
            }
        }
    }
}