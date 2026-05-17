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

include(":composeApp", ":core:common", ":core:domain", ":core:ui", ":core:data", ":features:shared", ":features:help", ":features:profile", ":features:planning", ":features:labels", ":features:offlinesync", ":features:saleshistory", ":features:expiry", ":features:auth", ":features:movements", ":features:bulkimport", ":features:stockcount", ":features:customers", ":features:suppliers", ":features:imports", ":features:users", ":features:reports", ":features:ky", ":features:stock", ":features:sell", ":features:settings", ":features:test-fixtures")
