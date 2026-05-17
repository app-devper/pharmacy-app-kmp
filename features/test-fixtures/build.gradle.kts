plugins {
    id("pharmacy.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:domain"))

            implementation(libs.kotlinx.coroutines.core)
        }
    }
}

android {
    namespace = "app.devper.pharm.features.testfixtures"
}
