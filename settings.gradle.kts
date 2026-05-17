pluginManagement {
    includeBuild("build-logic")
    repositories {
        mavenCentral()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
    }
}

rootProject.name = "PharmacyApp"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":composeApp", ":core:common", ":core:domain", ":core:ui", ":core:data", ":features", ":features:shared", ":features:help", ":features:profile", ":features:planning", ":features:labels", ":features:offlinesync")
