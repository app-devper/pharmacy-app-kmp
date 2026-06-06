plugins {
    id("pharmacy.kmp.compose.library")
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
        }
    }
}

android {
    namespace = "app.devper.pharm.features.saleshistory"
}
