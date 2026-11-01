@file:Suppress("DEPRECATION")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidKotlinMultiplatformLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    id("pharmacy.architecture.audit")
}

kotlin {
    jvmToolchain(17)

    // Shared UI as an Android library; the installable app is :androidApp.
    android {
        namespace = "app.devper.pharm.shared"
        compileSdk = libs.versions.android.compileSdk.get().toInt()
        minSdk = libs.versions.android.minSdk.get().toInt()
        compilerOptions { jvmTarget.set(JvmTarget.JVM_17) }
    }
    jvm()

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
    ).forEach { target ->
        target.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName.set("composeApp")
        browser {
            commonWebpackConfig {
                outputFileName = "composeApp.js"
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            api(project(":core:common")) // :androidApp wires the platform modules
            api(project(":core:domain")) // buildHttpClient signature reaches :androidApp
            implementation(project(":core:ui"))
            api(project(":core:data"))
            implementation(project(":features:help"))
            implementation(project(":features:profile"))
            implementation(project(":features:planning"))
            implementation(project(":features:labels"))
            implementation(project(":features:offlinesync"))
            implementation(project(":features:saleshistory"))
            implementation(project(":features:expiry"))
            implementation(project(":features:auth"))
            implementation(project(":features:movements"))
            implementation(project(":features:bulkimport"))
            implementation(project(":features:stockcount"))
            implementation(project(":features:customers"))
            implementation(project(":features:suppliers"))
            implementation(project(":features:imports"))
            implementation(project(":features:users"))
            implementation(project(":features:reports"))
            implementation(project(":features:ky"))
            implementation(project(":features:stock"))
            implementation(project(":features:sell"))
            implementation(project(":features:settings"))

            implementation(compose.runtime)
            implementation(compose.material3)
            implementation(libs.compose.ui.tooling.preview)

            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.androidx.navigation.compose)

            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)

            implementation(libs.ktor.client.core)

            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)

            api(libs.multiplatform.settings)
        }

        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.kotlinx.coroutines.swing)
            implementation(libs.ktor.client.java)
        }

        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.kotlinx.browser)
            implementation(npm("@js-joda/core", "5.6.0"))
            implementation(npm("@js-joda/timezone", "2.18.2"))
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.ktor.client.mock)
            implementation(libs.koin.core)
        }

        jvmTest.dependencies {
            implementation(compose.desktop.uiTestJUnit4)
        }
    }
}


compose.resources {
    generateResClass = org.jetbrains.compose.resources.ResourcesExtension.ResourceClassGeneration.Never
}

compose.desktop {
    application {
        mainClass = "app.devper.pharm.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "PharmacyApp"
            packageVersion = "1.0.0"
        }
    }
}
