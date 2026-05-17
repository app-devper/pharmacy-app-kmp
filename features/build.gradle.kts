plugins {
    id("pharmacy.kmp.compose.library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:ui"))
            implementation(project(":features:shared"))

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.savedstate)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.bundles.ktor.common)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            implementation(libs.multiplatform.settings)
            implementation(libs.kermit)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(project(":features:profile"))
            implementation(project(":features:labels"))
            implementation(project(":features:offlinesync"))
            implementation(project(":features:stockcount"))
            implementation(project(":features:customers"))
            implementation(project(":features:suppliers"))
            implementation(project(":features:imports"))
            implementation(project(":features:users"))
        }
    }
}

android {
    namespace = "app.devper.pharm.features"
}

compose.resources {
    packageOfResClass = "app.devper.pharm.features.resources"
}
