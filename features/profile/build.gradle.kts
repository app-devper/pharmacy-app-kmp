plugins {
    id("pharmacy.kmp.compose.library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:ui"))

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
        }
            commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":features:test-fixtures"))
        }
    }
}

android {
    namespace = "app.devper.pharm.features.profile"
}
