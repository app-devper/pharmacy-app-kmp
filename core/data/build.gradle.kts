plugins {
    id("pharmacy.kmp.library")
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:common"))
            implementation(project(":core:domain"))

            implementation(libs.bundles.ktor.common)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)

            implementation(libs.koin.core)
            implementation(libs.multiplatform.settings)
            implementation(libs.kermit)
        }

        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

android {
    namespace = "app.devper.pharm.core.data"
}
