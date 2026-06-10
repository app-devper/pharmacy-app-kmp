import kotlinx.kover.gradle.plugin.dsl.CoverageUnit

plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.buildkonfig) apply false
    alias(libs.plugins.kover)
}

subprojects {
    apply(plugin = "org.jetbrains.kotlinx.kover")
}

dependencies {
    kover(project(":core:common"))
    kover(project(":core:domain"))
    kover(project(":core:ui"))
    kover(project(":core:data"))
    kover(project(":features:auth"))
    kover(project(":features:bulkimport"))
    kover(project(":features:customers"))
    kover(project(":features:expiry"))
    kover(project(":features:help"))
    kover(project(":features:imports"))
    kover(project(":features:ky"))
    kover(project(":features:labels"))
    kover(project(":features:movements"))
    kover(project(":features:offlinesync"))
    kover(project(":features:planning"))
    kover(project(":features:profile"))
    kover(project(":features:reports"))
    kover(project(":features:saleshistory"))
    kover(project(":features:sell"))
    kover(project(":features:settings"))
    kover(project(":features:stock"))
    kover(project(":features:stockcount"))
    kover(project(":features:suppliers"))
    kover(project(":features:users"))
}

kover {
    reports {
        filters {
            excludes {
                packages(
                    "app.devper.pharm.ui.i18n.groups",
                    "app.devper.pharm.ui.print",
                )
                classes(
                    "*Screen",
                    "*ScreenKt",
                    "*Content",
                    "*ContentKt",
                    "*ComposableSingletons*",
                    "*Dto",
                    "*DtoKt",
                )
                annotatedBy("androidx.compose.runtime.Composable")
            }
        }
        verify {
            rule {
                bound {
                    minValue = COVERAGE_FLOOR
                    coverageUnits = CoverageUnit.LINE
                }
            }
        }
    }
}

val COVERAGE_FLOOR = 50
