// Root build script — keeps repository config in settings.gradle.kts and
// declares plugins as `apply false` so subprojects can opt in via their own
// build files. Add common plugins (e.g. ktlint) here later if desired.
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
}
