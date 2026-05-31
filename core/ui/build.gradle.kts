plugins {
    id("pharmacy.kmp.compose.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:domain"))

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.savedstate)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kotlinx.datetime)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
        }
    }
}

android {
    namespace = "app.devper.pharm.core.ui"
}

compose.resources {
    packageOfResClass = "app.devper.pharm.ui.resources"
}
