import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("org.jetbrains.kotlin.multiplatform")
}

// Applied imperatively, not in the plugins block: Gradle generates this script's accessors by
// applying its plugins to a synthetic project that does not read our gradle.properties, and
// AGP 9 refuses com.android.library next to kotlin.multiplatform unless android.builtInKotlin
// and android.newDsl are off. The real projects have both flags.
apply(plugin = "com.android.library")

val libs = the<LibrariesForLibs>()

extensions.configure<KotlinMultiplatformExtension>("kotlin") {
    jvmToolchain(17)

    androidTarget()
    jvm()
    iosArm64()
    iosSimulatorArm64()

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    sourceSets.named("commonTest") {
        dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

extensions.configure<LibraryExtension>("android") {
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
