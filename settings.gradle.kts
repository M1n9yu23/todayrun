pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/") }
    }
}

rootProject.name = "GyuRun"
include(":app")
include(":core:common")
include(":core:connectivity:domain")
include(":core:domain")
include(":core:database")
include(":core:datastore")
include(":core:data")
include(":core:network")
include(":core:navigation")
include(":core:map")
include(":core:presentation:designsystem")
include(":core:presentation:ui")
include(":feature:onboarding:presentation:api")
include(":feature:active:presentation:api")
include(":feature:overview:presentation:api")
include(":feature:details:presentation:api")
include(":feature:stats:presentation:api")
include(":feature:settings:presentation:api")
include(":feature:onboarding:presentation:impl")
include(":feature:active:presentation:impl")
include(":feature:overview:presentation:impl")
include(":run:domain")
include(":run:location")
include(":run:sensor")