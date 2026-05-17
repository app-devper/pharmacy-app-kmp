plugins {
    id("pharmacy.kmp.library")
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            api(project(":core:common"))
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.koin.core)
        }
    }
}

android {
    namespace = "app.devper.pharm.domain"
}
