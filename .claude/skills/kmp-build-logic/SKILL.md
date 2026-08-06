---
name: kmp-build-logic
description: The build infrastructure of the pharmacy app — the two convention plugins in build-logic/, the version catalog, the auditArchitecture task and its 11 rules, Kover coverage, and the Cloud Build deploy. Use when adding a module, changing a dependency, or extending the audit ruleset.
---

# kmp-build-logic

Four pieces keep 26 modules identical:

1. **Convention plugins** in the `build-logic/` included build.
2. **Version catalog** `gradle/libs.versions.toml`.
3. **`auditArchitecture`** — greps source for rule violations and fails the build.
4. **Kover** — a line-coverage floor enforced by `koverVerify`.

Stack: Kotlin 2.3.21 / Compose Multiplatform 1.11.0 / AGP 8.13.2 / Gradle 8.14.3 /
Ktor 3.5.0 / Koin 4.2.1 / Kover 0.9.1. JDK 17. Targets: `androidTarget`, `jvm`,
`iosArm64`, `iosSimulatorArm64`, `wasmJs`. compileSdk 36, minSdk 24.

## 1. `build-logic/`

```kotlin
// settings.gradle.kts
pluginManagement { includeBuild("build-logic") }

// build-logic/build.gradle.kts
plugins { `kotlin-dsl` }
dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
```

That last line is what makes `the<LibrariesForLibs>()` — the `libs` catalog —
usable inside the precompiled script plugins.

### `pharmacy.kmp.library`

Applies `kotlin.multiplatform` + `com.android.library`, sets `jvmToolchain(17)`,
declares all five targets, adds `libs.kotlin.test` to `commonTest`, and
configures Android `compileSdk` / `minSdk` / Java 17 compatibility from the
catalog.

Used by the pure modules: `:core:common`, `:core:domain`, `:core:data`,
`:features:test-fixtures`.

### `pharmacy.kmp.compose.library`

Applies `pharmacy.kmp.library` + `org.jetbrains.compose` +
`org.jetbrains.kotlin.plugin.compose`, adds the Compose deps to `commonMain`
(runtime, foundation, material3, materialIconsExtended, ui,
components.resources, ui-tooling-preview, lifecycle-runtime-compose) and
`compose.uiTooling` to `androidMain`, enables `publicResClass` /
`generateResClass`, and points the Compose compiler at
`compose_compiler_config.conf` for stability configuration.

Used by `:core:ui` and all 20 `:features:<x>`.

### `pharmacy.architecture.audit`

Registers `auditArchitecture` and wires it into `check`. Applied by
`:composeApp` only — the task walks from `rootProject.projectDir`, so one
application covers the whole repo.

### Per-module build files

```kotlin
// core/domain/build.gradle.kts
plugins { id("pharmacy.kmp.library") }

// features/<x>/build.gradle.kts
plugins {
    id("pharmacy.kmp.compose.library")
    alias(libs.plugins.kotlin.serialization)   // required if the module declares @Serializable routes
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:ui"))
            implementation(libs.androidx.lifecycle.viewmodel)
            implementation(libs.androidx.navigation.compose)
            implementation(libs.koin.core)
            implementation(libs.koin.compose.viewmodel)
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":features:test-fixtures"))
        }
    }
}
android { namespace = "app.devper.pharm.features.<x>" }
```

**Gotcha**: a module declaring `@Serializable` routes without the serialization
plugin compiles green and throws `SerializationException` at runtime.

## 2. Version catalog

`gradle/libs.versions.toml` is the single source of versions — nothing is
pinned in a module build file. Two keys matter beyond dependencies:

- **`app-version`** — the deploy pipeline tags `v<app-version>` on merge to
  `main`. **Bump it in every release/hotfix PR** or the deploy runs but skips
  tagging.
- **`app-versionCode`** — the Android version code.

## 3. `auditArchitecture`

`build-logic/src/main/kotlin/pharmacy.architecture.audit.gradle.kts` — ~240
lines of file walking. Writes `build/reports/architecture-audit.txt` and throws
`GradleException` listing every violation with `file:line`.

| Rule | Scope | Fails on |
|---|---|---|
| A10 | `core/**` | `import app.devper.pharm.presentation.` |
| A17 | everything | `import app.devper.pharm.domain.common.` (removed package) |
| A19 | everything | stale pre-split `:core:ui` packages (`presentation.theme`, `presentation.designsystem`, `presentation.common.Base*`, `presentation.components.{AppShell,ErrorBottomSheet,WindowSize}`, `presentation.format.`, `scanner.`, …) |
| A20 | `features/**` | `import app.devper.pharm.data.` |
| A23 | `features/**/di/*Module.kt` | importing `domain.usecase` / `domain.observer` / `domain.parser` |
| A24 | `core/data/**/*Dto.kt` | a `val` line inside a `@Serializable` block without `@SerialName` |
| A25 | `core/data/**/*Dto.kt` | a snake_case Kotlin property name |
| A26 | `core/*`, `features` | any `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` / platform test folder |
| A27 | `core`, `features`, `composeApp` | `expect class/fun/val/var/object/interface/typealias` |
| A28 | `core`, `features` production | `throw` or `Result.failure(` of `IllegalStateException` / `IllegalArgumentException` / `RuntimeException` / `Exception` / `UnsupportedOperationException` / `NullPointerException` |
| A29 | `core/ui`, `core/domain`, `features`, `composeApp` commonMain | a Thai string literal |

A28 skips `commonTest` / `jvmTest` / `androidUnitTest` and
`:features:test-fixtures`. A29 skips `i18n/groups/`, `ui/print/`, filenames
containing `Preview`, `:features:test-fixtures`, lines containing `.contains(`,
everything after the first `@Preview` or `private val sample*`/`preview*`
marker, and a hardcoded `a29AllowedFiles` allowlist (12 domain/VM files holding
Thai stored-data defaults).

### Extending the ruleset

The task is a series of `grepFiles(label, dir, regex)` calls plus a few bespoke
walkers. To add a rule: take the next `A<n>`, add the call, and update the task
`description`, `kmp-review`'s P0 table and `kmp-code-pattern` §6. Numbers stay
stable — PR comments cite them.

Deliberately **not** audited, because they need parsing rather than grepping:
raw Material 3 usage, hex colors outside `theme/`, comments, `!!`, file length,
and the file-per-class rule. Those live in `kmp-review`.

## 4. Kover

The root `build.gradle.kts` applies Kover to every subproject and aggregates
the 24 measured modules (everything but `:composeApp` and
`:features:test-fixtures`).

```kotlin
val COVERAGE_FLOOR = 55
```

`koverVerify` enforces that as a **line-coverage minimum**. It is a ratchet
toward 80, not a fixed gate — raise it in the same PR that adds the tests.
Excluded from measurement: packages `ui.i18n.groups` and `ui.print`, classes
`*Screen` / `*Content` / `*ComposableSingletons*` / `*Dto`, and anything
annotated `@Composable`. What remains is domain / use case / VM / mapper /
localizer.

`./gradlew koverHtmlReport` → `build/reports/kover/html/`.

## 5. Settings

```kotlin
include(
    ":composeApp",
    ":core:common", ":core:domain", ":core:ui", ":core:data",
    ":features:test-fixtures",
    ":features:auth", ":features:bulkimport", … ":features:users",
)
```

26 entries. `:composeApp/build.gradle.kts` applies
`pharmacy.architecture.audit` so the task exists.

## 6. Deploy

Merging to `main` fires the Cloud Build trigger `deploy-pharm-app` (project
`devperpos`, config `cloudbuild.yaml`): it builds
`:composeApp:wasmJsBrowserDistribution`, deploys to Firebase Hosting site
`pharm-app`, and pushes tag `v<app-version>` if that tag doesn't exist yet.

Manual:
```bash
./gradlew :composeApp:wasmJsBrowserDistribution && firebase deploy --only hosting:pharm-app
```

The required CI check on both `main` and `develop` is
`Linux (JVM + Android + WasmJs + audit)`.

## 7. Verify

```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :core:{common,domain,ui,data}:jvmTest \
          :features:{auth,bulkimport,customers,expiry,help,imports,ky,labels,movements,offlinesync,planning,profile,reports,saleshistory,sell,settings,stock,stockcount,suppliers,users}:jvmTest \
          koverVerify
```

Quick smoke: `./gradlew :composeApp:check`.
