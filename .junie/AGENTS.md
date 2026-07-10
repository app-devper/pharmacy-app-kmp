# Development Guide for PharmacyApp (KMP)

This document provides project-specific details for advanced developers working on the PharmacyApp Kotlin Multiplatform (KMP) project.

## 1. Build & Configuration

The project follows a modular 26-module Gradle layout.

### Module Structure
- `:composeApp`: Entry point, app shell, navigation, and ONLY module with platform source folders (`iosMain`, `androidMain`, etc.).
- `:features:<x>`: 20 independent feature modules (e.g., `:features:sell`, `:features:stock`).
- `:core:{common,domain,ui,data}`: Layered architecture.
- `:features:test-fixtures`: Shared test fakes for feature tests.

### Important Build Commands
- **Full Check**: `./gradlew :composeApp:check`
- **Architecture Audit**: `./gradlew :composeApp:auditArchitecture` (Enforces module boundaries and forbidden imports).
- **Run Targets**:
  - Android: `./gradlew :composeApp:assembleDebug`
  - Desktop (JVM): `./gradlew :composeApp:run`
  - Web: `./gradlew :composeApp:wasmJsBrowserDevelopmentRun`
  - iOS Simulator: `./gradlew :composeApp:iosSimulatorArm64Test`

### Dependency Rules
- `:core:*` must NOT import from `:features:*`.
- `:features:<x>` must NOT import from `:core:data` or other features.
- Inject interfaces from `:core:domain`; never use concrete `RepositoryImpl` from `:core:data` in features.

## 2. Testing Information

The project maintains high testing standards with over 900 tests and a strict architecture audit.

### Running Tests
- **Canonical Sweep**:
  ```bash
  ./gradlew :composeApp:auditArchitecture :composeApp:testDebugUnitTest \
            :composeApp:compileTestKotlinIosSimulatorArm64 \
            :composeApp:compileTestKotlinWasmJs \
            :core:{common,domain,ui,data}:jvmTest \
            :features:{auth,bulkimport,customers,expiry,help,imports,ky,labels,movements,offlinesync,planning,profile,reports,saleshistory,sell,settings,stock,stockcount,suppliers,users}:jvmTest
  ```
- **Coverage (Kover)**: `./gradlew koverVerify`. The coverage floor is strictly enforced.

### Adding New Tests
- Tests live in `commonTest` source sets of the respective modules.
- Use `Fake*Repository` from `:features:test-fixtures` for feature-level integration tests.
- **Example**: To test domain logic in `core:domain`, add tests to `core/domain/src/commonTest/kotlin/...`.

### Test Example (Demonstration)
A simple test for `PriceExt.kt` logic:
```kotlin
package app.devper.pharm.domain.extension

import app.devper.pharm.common.value.Money
import kotlin.test.Test
import kotlin.test.assertEquals

class PriceExtTest {
    @Test
    fun testResolvePrice_returnsBaseWhenPricesEmpty() {
        val base = Money(100.0)
        val result = resolvePrice(base, emptyMap(), Tier.Wholesale)
        assertEquals(base, result)
    }
}
```

## 3. Additional Development Information

### Strict Style: NO COMMENTS
- **Rule**: Do NOT include any comments in `.kt` files (`//`, `/* */`, `/** */`, `TODO`, `FIXME`).
- **Rationale**: Code must be self-documenting via naming, types, and small functions. If explanation is needed, refactor the code instead.
- **Exception**: `@Suppress` annotations and required license headers.

### Navigation Architecture
- Two-level `NavHost` in `:composeApp`.
- Outer `AppNavHost` swaps between `authNav` and `MainRoot`.
- `MainShell` provides the UI shell (sidebar/topbar) for all feature pages.
- Features register their screens via `fun NavGraphBuilder.<x>Nav(...)` extensions.

### Architecture Constraints
- No `expect`/`actual` outside of `:composeApp` (rarely used).
- No generic exceptions in production.
- No Thai string literals in production UI code (must use `PharmStrings`).
- CI enforces `auditArchitecture` which blocks forbidden imports and structure violations.
