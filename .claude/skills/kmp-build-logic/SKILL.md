---
name: kmp-build-logic
description: Set up the build infrastructure for a Compose Multiplatform Clean-Architecture project — convention plugins (KMP library + Compose library), version catalog, settings.gradle.kts, and the auditArchitecture Gradle task that enforces module + DTO + platform rules. Use when bootstrapping a new KMP project or extending the audit ruleset.
---

# kmp-build-logic

The infrastructure layer that makes every module look the same and prevents drift from the rules
in **kmp-rules** + **kmp-code-pattern**.

Three pieces:
1. **Convention plugins** in an included build `build-logic/` — apply once per module.
2. **Version catalog** in `gradle/libs.versions.toml` — single source of dependency versions.
3. **`auditArchitecture` Gradle task** — greps source for rule violations, fails the build.

## 1. `build-logic/` included build

`settings.gradle.kts` (root):
```kotlin
pluginManagement { includeBuild("build-logic") }
```

`build-logic/build.gradle.kts`:
```kotlin
plugins { `kotlin-dsl` }
dependencies {
    implementation(libs.android.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.compose.gradle.plugin)
    implementation(libs.compose.compiler.gradle.plugin)
    implementation(files(libs.javaClass.superclass.protectionDomain.codeSource.location))
}
```

### Base KMP-library plugin — `build-logic/src/main/kotlin/<project>.kmp.library.gradle.kts`
```kotlin
plugins {
    id("org.jetbrains.kotlin.multiplatform")
    id("com.android.library")
}
extensions.configure<KotlinMultiplatformExtension>("kotlin") {
    jvmToolchain(17)
    androidTarget()
    jvm()
    iosArm64()
    iosSimulatorArm64()
    @OptIn(ExperimentalWasmDsl::class) wasmJs { browser() }
}
```
Apply to **pure-Kotlin** modules: `:core:common`, `:core:domain`, `:core:data`,
`:features:test-fixtures`.

### Compose-flavored plugin — `<project>.kmp.compose.library.gradle.kts`
```kotlin
plugins {
    id("<project>.kmp.library")
    id("org.jetbrains.compose")
    id("org.jetbrains.kotlin.plugin.compose")
}
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
    sourceSets.named("androidMain") {
        dependencies { implementation(ComposePlugin.Dependencies(project).uiTooling) }
    }
}
```
Apply to **Compose-aware** modules: `:core:ui`, every `:features:<x>`.

### Per-module build files
```kotlin
// core/domain/build.gradle.kts
plugins { id("<project>.kmp.library") }

// core/ui/build.gradle.kts
plugins { id("<project>.kmp.compose.library") }

// features/<x>/build.gradle.kts
plugins {
    id("<project>.kmp.compose.library")
    alias(libs.plugins.kotlin.serialization)         // ← REQUIRED if the module declares @Serializable routes / DTOs
}
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(project(":core:domain"))
            implementation(project(":core:ui"))
        }
        commonTest.dependencies {
            implementation(libs.kotlinx.coroutines.test)
            implementation(project(":features:test-fixtures"))   // when using shared fakes
        }
    }
}
android { namespace = "<base>.features.<x>" }
```

**Gotcha**: a feature module declaring `@Serializable` route objects but missing the
`kotlin.serialization` plugin → compile passes, runtime throws `SerializationException:
Serializer for class '<Route>' is not found`. Common foot-gun.

## 2. Version catalog — `gradle/libs.versions.toml`

Single source of dependency versions. Pin everything:

```toml
[versions]
kotlin = "2.3.21"
kotlinx-coroutines = "1.10.2"
kotlinx-serialization-json = "1.11.0"
compose = "1.11.0"
ktor = "3.3.1"
koin = "4.2.0"
agp = "8.13.2"

[libraries]
kotlin-gradle-plugin = { module = "org.jetbrains.kotlin:kotlin-gradle-plugin", version.ref = "kotlin" }
compose-gradle-plugin = { module = "org.jetbrains.compose:compose-gradle-plugin", version.ref = "compose" }
compose-compiler-gradle-plugin = { module = "org.jetbrains.kotlin:compose-compiler-gradle-plugin", version.ref = "kotlin" }
android-gradle-plugin = { module = "com.android.tools.build:gradle", version.ref = "agp" }

kotlinx-coroutines-test = { module = "org.jetbrains.kotlinx:kotlinx-coroutines-test", version.ref = "kotlinx-coroutines" }
kotlinx-serialization-json = { module = "org.jetbrains.kotlinx:kotlinx-serialization-json", version.ref = "kotlinx-serialization-json" }

ktor-client-core = { module = "io.ktor:ktor-client-core", version.ref = "ktor" }
ktor-client-content-negotiation = { module = "io.ktor:ktor-client-content-negotiation", version.ref = "ktor" }
ktor-serialization-kotlinx-json = { module = "io.ktor:ktor-serialization-kotlinx-json", version.ref = "ktor" }

koin-core = { module = "io.insert-koin:koin-core", version.ref = "koin" }
koin-compose-viewmodel = { module = "io.insert-koin:koin-compose-viewmodel", version.ref = "koin" }

# … etc

[plugins]
kotlin-multiplatform = { id = "org.jetbrains.kotlin.multiplatform", version.ref = "kotlin" }
kotlin-serialization = { id = "org.jetbrains.kotlin.plugin.serialization", version.ref = "kotlin" }
```

## 3. `auditArchitecture` Gradle task

A precompiled script plugin `<project>.architecture.audit.gradle.kts` that adds an
`auditArchitecture` task. Apply it to `:composeApp` (or any module that depends on every module
you want audited).

Skeleton:
```kotlin
tasks.register("auditArchitecture") {
    group = "verification"
    description = "Audits inward-only module rules, DTO conventions, platform ownership, no expect/actual, typed errors."

    val projectRoot = rootProject.projectDir
    val outputFile = layout.buildDirectory.file("reports/architecture-audit.txt")
    outputs.upToDateWhen { false }
    outputs.file(outputFile)

    doLast {
        val violations = mutableListOf<String>()

        // RULE: :core:* must not import a features/<x> presentation package
        val coreRoot = projectRoot.resolve("core")
        coreRoot.walkTopDown()
            .filter { it.isFile && it.extension == "kt" && !it.absolutePath.contains("/build/") }
            .forEach { f ->
                f.useLines { lines ->
                    lines.forEachIndexed { i, line ->
                        if (line.matches(Regex("^import <base>\\.presentation\\..*"))) {
                            violations += "core-imports-presentation  ${f.relativeTo(projectRoot)}:${i + 1}  ${line.trim()}"
                        }
                    }
                }
            }

        // RULE: DTOs must use @SerialName + camelCase Kotlin names
        val dtoRoot = projectRoot.resolve("core/data/src/commonMain/kotlin")
        // … grep `@Serializable data class` blocks, check each `val foo: T,` line is camelCase
        // and the line above is `@SerialName("…")`. If not, add to violations.

        // RULE: platform folders only inside :composeApp
        listOf("core", "features").forEach { topDir ->
            val root = projectRoot.resolve(topDir)
            if (root.exists()) {
                root.walkTopDown()
                    .filter { it.isDirectory && it.name in setOf("androidMain", "iosMain", "jvmMain", "wasmJsMain") }
                    .forEach { d ->
                        violations += "platform-folder-outside-composeApp  ${d.relativeTo(projectRoot)}"
                    }
            }
        }

        // RULE: no expect/actual anywhere
        val expectRe = Regex("""^\s*(expect\s+(class|fun|val|var|object)|actual\s+(class|fun|val|var|object))\b""")
        // walk everything; grep for expectRe; report.

        // RULE: no generic exception in production
        val genericExceptionRe = Regex(
            """\b(throw|Result\.failure\()\s*(IllegalStateException|IllegalArgumentException|RuntimeException|Exception|UnsupportedOperationException|NullPointerException)\("""
        )
        listOf("core", "features").forEach { topDir ->
            val root = projectRoot.resolve(topDir)
            if (!root.exists()) return@forEach
            root.walkTopDown()
                .filter { it.isFile && it.extension == "kt" && !it.absolutePath.contains("/build/") }
                .filter { f -> !f.absolutePath.let { it.contains("/commonTest/") || it.contains("/jvmTest/") } }
                .filter { f -> !f.absolutePath.contains("/features/test-fixtures/") }
                .forEach { f ->
                    f.useLines { lines ->
                        lines.forEachIndexed { i, line ->
                            if (genericExceptionRe.containsMatchIn(line)) {
                                violations += "generic-exception-in-production  ${f.relativeTo(projectRoot)}:${i + 1}  ${line.trim()}"
                            }
                        }
                    }
                }
        }

        // RULE: file-per-class — no two of {Screen, Content, ViewModel, UiState} in the same file
        // …

        val report = outputFile.get().asFile
        report.parentFile.mkdirs()
        report.writeText(violations.joinToString("\n"))

        if (violations.isNotEmpty()) {
            throw GradleException(
                "auditArchitecture: ${violations.size} violation(s):\n" + violations.joinToString("\n  ", prefix = "  ")
            )
        }
    }
}
```

Each rule corresponds to a numbered audit code (`A10`, `A20`, `A24`, `A26`, `A27`, `A28`,
file-per-class, …) — see **kmp-review**'s P0 table. The numbers are stable so PR comments can
reference them.

## 4. Settings + `:composeApp` setup

`settings.gradle.kts`:
```kotlin
pluginManagement { includeBuild("build-logic") }

include(":composeApp",
        ":core:common", ":core:domain", ":core:ui", ":core:data",
        ":features:test-fixtures",
        ":features:auth", ":features:<feat1>", … )
```

`:composeApp/build.gradle.kts` applies `<project>.architecture.audit` in its plugins block (so
the task is registered).

## 5. Verify

```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs
```
A green sweep means: layout compiles on every target, audit rules hold, tests pass.
