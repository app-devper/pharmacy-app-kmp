import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.compose.ComposeExtension
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.compose.resources.ResourcesExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("pharmacy.kmp.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}

val libs = the<LibrariesForLibs>()

extensions.configure<KotlinMultiplatformExtension>("kotlin") {
    sourceSets.named("commonMain") {
        dependencies {
            val compose = ComposePlugin.Dependencies(project)
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.lifecycle.runtime.compose)
        }
    }
}

extensions.configure<ComposeExtension>("compose") {
    extensions.configure<ResourcesExtension>("resources") {
        publicResClass = true
        generateResClass = ResourcesExtension.ResourceClassGeneration.Always
    }
}
