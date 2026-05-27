# CLAUDE.md — pharmacy-app KMP companion

Project-scoped instructions for Claude Code when working in
`/Users/admin/ProjectPos/pharmacy-app/app-kmp/`. Applies to the KMP
companion app (Compose Multiplatform — **27-module Gradle layout** after
the per-feature split arc `5b4d0ed` → `9a76123`):
`:composeApp` + `:core:{common,domain,ui,data}` + `:features:shared` +
`:features:test-fixtures` + 20 `:features:<x>` modules.

## Module structure

```
:composeApp                              entry point + ONLY module with platform source folders
  ├─ App.kt + AppViewModel + AppNavHost
  ├─ MainActivity / MainViewController / Main (jvm/wasm)
  ├─ di/AppModule.kt                     composition root — includes(commonModule, domainModule, dataModule,
  │                                       + 20 per-feature modules)
  ├─ <plat>Main/platform/FileDownloaderImpl.kt × 4   platform impls of FileDownloader interface
  ├─ <plat>Main/platform/ReceiptPrinterImpl.kt × 4   platform impls of ReceiptPrinter interface
  └─ <plat>Main bindings: each Main*.kt's platform Koin module binds Settings + HttpClient engine +
        AppDispatchers (platform-appropriate IO) + FileDownloaderImpl + ReceiptPrinterImpl
  deps: :core:{common,domain,ui,data} + :features:shared + all 20 :features:<x>

:core:common                             interfaces + pure infra (commonMain + commonTest only)
  ├─ common/ AppDispatchers (data class only, constructed per-platform)
  ├─ common/ Logger, PrintlnLogger
  ├─ common/ AppException + 9 typed subclasses (Auth, Forbidden, NotFound, Conflict,
  │                                              Network, Server, Validation, Storage,
  │                                              UnsupportedPlatform)
  ├─ common/print/ ReceiptTemplate (data) + ReceiptPrinter (interface)
  ├─ common/platform/ FileDownloader (interface) + MimeType constants
  ├─ common/di/CommonModule.kt           (Koin module — binds Logger only)
  ├─ domain/usecase/ BaseUseCase + BaseSyncUseCase
  package: app.devper.pharm.common (+ app.devper.pharm.domain.usecase for BaseUseCase)
  deps: kotlinx only (zero project deps) + koin-core
  rule: NO platform source folders. Every platform-bound concern is an interface here;
        impls live in :composeApp/<plat>Main and are Koin-bound per platform.

:core:domain                             pure domain — commonMain + commonTest only
  ├─ model/ param/ repository/ usecase/
  ├─ parser/ util/ pricing/ event/ observer/
  ├─ di/DomainModule.kt                  (Koin module — 76+ bindings via 10 sub-modules)
  deps: :core:common (+ kotlinx + koin-core)
  rule: ไม่มี androidMain / iosMain / jvmMain / wasmJsMain folder
  note: api(:core:common) — re-exports so :features:<x> เห็น :core:common ทาง transitively

:core:ui                                 shared compose infra (commonMain + commonTest only)
  ├─ ui/theme/ designsystem/ common/ components/
  ├─ ui/format/ scanner/ print/ help/
  ├─ composeResources/font/sarabun_*.ttf
  packages: app.devper.pharm.ui.*
  packageOfResClass = "app.devper.pharm.ui.resources"
  deps: compose + :core:common + :core:domain
  rule: ไม่มี platform source folders

:core:data                               repository impls + transport (commonMain + commonTest only)
  ├─ data/network/ storage/ repository/
  ├─ data/remote/api/ remote/dto/
  ├─ data/di/DataModule.kt               (Apis + RepositoryImpls — :composeApp includes it)
  deps: ktor + multiplatform-settings + koin-core + :core:common + :core:domain
  rule: ไม่มี platform source folders

:features:shared                         navigation hub + route data objects
  ├─ presentation/<feature>/<Feature>Routes.kt × 20   @Serializable data objects
  │    (Sell, Stock, Help, LabelPrint, etc. — one per per-feature module)
  ├─ presentation/navigation/ShelledScreen.kt    public AppShell + MAIN_NAV table
  deps: :core:domain + :core:ui ONLY
  rule: NO knowledge of :features:<x> or :composeApp (would be a cycle).
        Declares routes that :features:<x>'s NavGraphs reference and
        :composeApp's AppNavHost uses for startDestination + nav.

:features:test-fixtures                  shared test doubles (commonMain only, test-only module)
  ├─ domain/repository/Fake{Cart,Customer,Drug,Ky,Label,OfflineSaleQueue,
  │                          Profile,PurchaseOrder,Sale,Settings,StockCounts,
  │                          Supplier,UiPreferences,Users}Repository.kt   (14 fakes)
  deps: :core:common + :core:domain + kotlinx-coroutines-core only
  rule: Fakes live in commonMain (not commonTest) so any feature's commonTest
        can `implementation(project(":features:test-fixtures"))` and import.
        A28 audit rule excludes /features/test-fixtures/ — fakes throw
        RuntimeException as a deliberate test signal.

:features:<x>                            20 per-feature modules
  ├─ build.gradle.kts                    pharmacy.kmp.compose.library
  ├─ src/commonMain/kotlin/app/devper/pharm/
  │   ├─ di/<Feature>Module.kt           ONLY VM `factoryOf` bindings
  │   └─ presentation/<feature>/         Screen/Content/Callbacks/ViewModel/
  │                                      UiState/NavGraph + section/components/
  │                                      sibling subdirs as needed
  ├─ src/commonTest/kotlin/...           (when tests exist)
  │   └─ presentation/<feature>/         VM tests + co-located fakes (when single-consumer)
  └─ composeResources/                   (only :features:help ships an asset today)
  deps: :core:domain + :core:ui + :features:shared (+ kotlinx-datetime when needed)
  test deps: :features:test-fixtures (when tests use shared fakes)

  The 20 modules: auth · bulkimport · customers · expiry · help · imports · ky ·
                  labels · movements · offlinesync · planning · profile · reports ·
                  saleshistory · sell · settings · stock · stockcount · suppliers · users
```

### Per-feature split migration recipe (for adding a brand-new feature)

The 6-step recipe was pilot-tested on `:features:help` (`26d9589`) and
then repeated 19 more times. Average time: ~20-30 minutes once the
foundation is in place.

1. **Carve out the module** — `mkdir -p features/<feat>/src/commonMain/kotlin/app/devper/pharm/presentation/<feat>/`
2. **Add `features/<feat>/build.gradle.kts`** mirroring an existing leaf
   feature (`:features:help` is the smallest template: apply
   `pharmacy.kmp.compose.library`, depend on `:core:domain` + `:core:ui`
   + `:features:shared`). Set unique
   `compose.resources { packageOfResClass = "app.devper.pharm.features.<feat>.resources" }`
   only if the feature ships its own assets.
3. **Register in `settings.gradle.kts`** — append `:features:<feat>` to
   `include(...)`.
4. **Add the Route data object** to `:features:shared` — one file at
   `features/shared/src/commonMain/kotlin/app/devper/pharm/presentation/<feat>/<Feat>Routes.kt`
   with `@Serializable data object Feat` (and any sub-routes).
5. **Build the feature production code** in
   `features/<feat>/.../presentation/<feat>/` (Screen, Content, Callbacks,
   ViewModel, UiState, NavGraph) + `.../di/<Feat>Module.kt` with the
   VM `factoryOf` binding.
6. **Wire from `:composeApp`**:
   - `composeApp/build.gradle.kts`: `implementation(project(":features:<feat>"))`
   - `composeApp/.../presentation/navigation/AppNavHost.kt`: append `<feat>Graph(navController, …)`
   - `composeApp/.../di/AppModule.kt`: append `<feat>Module` to `includes(...)`
   - `features/shared/.../navigation/ShelledScreen.kt`: register in `MAIN_NAV` if the feature gets a sidebar item

If the feature has tests using shared fakes, add to
`features/<feat>/build.gradle.kts`:

```kotlin
commonTest.dependencies {
    implementation(libs.kotlinx.coroutines.test)
    implementation(project(":features:test-fixtures"))
}
```

If a fake is single-consumer (only this feature's tests use it),
co-locate it under `features/<feat>/src/commonTest/.../domain/repository/`
instead of growing `:features:test-fixtures`.

**Forbidden (P0 — Kotlin compile errors + auditArchitecture task):**

The `auditArchitecture` Gradle task enforces 10 rules. Cross-feature
boundaries are also enforced implicitly by Kotlin's module system —
any import without a declared `project(...)` dep is a compile error.

Layering (forbidden imports):
- **A10**  `:core:*` importing from `:features:*`
- **A17**  stale `app.devper.pharm.domain.common` imports (post-split)
- **A19**  stale `:core:ui` package paths (pre-rename leftovers)
- **A20**  `:features:*` importing from `:core:data` (use `:core:domain` interface)
- **A23**  `:features:<x>/di/<X>Module.kt` importing non-VM types

Wire / source-set discipline:
- **A24**  DTO property missing explicit `@SerialName`
- **A25**  DTO property using snake_case Kotlin name
- **A26**  platform source folders outside `:composeApp`
- **A27**  `expect`/`actual` declarations anywhere in the project
- **A28**  generic `Exception` / `RuntimeException` / `IllegalStateException` in production

- `:core:common` ห้ามรู้จัก project module อื่นเลย (zero project deps)
- `:core:domain` ห้ามรู้จัก `:core:ui` / `:core:data` / `:features:*` / `:composeApp`
- `:core:*` ห้ามรู้จัก `:features:*` / `:composeApp`
- `:features:shared` ห้ามรู้จัก `:features:<x>` / `:composeApp` / `:core:data`
  (cycle — `:features:<x>` depends on `:features:shared`, not the reverse)
- `:features:test-fixtures` ห้ามรู้จัก `:features:<x>` / `:composeApp`
  (same reason — and it should stay minimal, only depending on
  `:core:common` + `:core:domain` + kotlinx)
- `:features:<x>` ห้ามรู้จัก `:core:data` (\*RepositoryImpl, \*Api, DTO —
  features ใช้ Repository interface จาก :core:domain เท่านั้น; data
  bindings อยู่ใน :core:data/.../di/DataModule.kt ซึ่ง :composeApp include เอง)
- `:features:<x>` ห้ามรู้จัก `:composeApp`
- `:features:<x>` ห้ามรู้จัก `:features:<y>` ที่เป็น production code —
  ใช้ Route object จาก `:features:shared` ถ้าต้อง navigate ข้าม feature
- **A26**: เฉพาะ `:composeApp` เท่านั้นที่มี platform source folders
  (`androidMain` / `iosMain` / `jvmMain` / `wasmJsMain`); module อื่นเป็น
  `commonMain` + `commonTest` ล้วน ๆ
- **A27**: ห้ามมี `expect class` / `expect fun` / `expect val` ที่ไหนในโปรเจกต์;
  ใช้ interface ใน `:core:common` + impl ใน `:composeApp/<plat>Main` แทน
- **A28**: ห้าม throw generic `IllegalStateException` / `RuntimeException` /
  `Exception` ใน production code — ใช้ typed `AppException` subclass
  (`:features:test-fixtures` ได้รับ exemption — fakes throw RuntimeException
  เป็น test signal เจตนา)

## Code style — NO COMMENTS

**ห้ามใส่ comment ใด ๆ ลงในไฟล์ `.kt` ทุกชนิด** — รวมถึง:

- `//` line comments
- `/* ... */` block comments
- `/** ... */` KDoc on classes, functions, properties, parameters
- TODO / FIXME / NOTE / HACK markers
- File-level header banners

Code ต้อง **self-documenting** ผ่านชื่อที่ดี + type signatures + small focused
functions. ถ้ารู้สึกว่าต้องอธิบายอะไร → rename + refactor แทนการเขียน comment.

This rule applies to:
- Production code in `composeApp/src/*`, `core/{domain,ui,data}/src/*`, `features/*/src/*` (all 22 feature/shared/test-fixtures modules)
- Test code in `*/src/commonTest`, `*/src/jvmTest`, `*/src/androidUnitTest`
- Fakes / fixtures / helpers in any test source set (including `:features:test-fixtures` despite its commonMain location)
- Build scripts (`build.gradle.kts`, `settings.gradle.kts`) — ถ้าต้อง explain
  ทำผ่าน clean structure แทน

**Exceptions** (case-by-case, ขอ confirm ก่อน):
- `@Suppress("...")` annotations — เป็น annotation ไม่ใช่ comment
- License header — ถ้า upstream library require (ไม่มีตอนนี้)
- Generated files under `build/` — ไม่ต้องแตะอยู่แล้ว
- Markdown / config / `.gradle.kts` plugin metadata — ไม่ใช่ Kotlin comment

When restyling / refactoring existing code: **strip comments as part of
the edit**. Don't preserve KDoc just because it was there. If a method's
purpose isn't clear from its name, rename the method.

### Why

- Phase-letter notes (M-CC) + split-arc commit history are living history
  in commit messages / PR descriptions, not buried in source
- Skill files (`pharmacy-kmp-*` SKILL.md) document patterns at a higher
  level; in-code comments duplicate that
- KMP cross-compiles 5 targets — every byte of source matters for context

## Project reminders (KMP-specific)

- **Stack**: Kotlin Multiplatform 2.3.21 / Compose Multiplatform 1.11.0 / AGP
  8.13.2 / Gradle 8.14.3
- **Targets**: `jvm`, `android`, `iosArm64`, `iosSimulatorArm64`, `wasmJs`
- **Modules (27)**: `:composeApp` (entry), `:core:{common,domain,ui,data}` (4 core),
  `:features:shared` (nav hub + routes), `:features:test-fixtures` (test doubles),
  20 `:features:<x>` (auth, bulkimport, customers, expiry, help, imports, ky,
  labels, movements, offlinesync, planning, profile, reports, saleshistory,
  sell, settings, stock, stockcount, suppliers, users). See `MODULE_GRAPH.md`
  for the full dep matrix.
- **Convention plugins** (`build-logic/`): each KMP library applies one of
  `id("pharmacy.kmp.library")` (pure data/domain — `:core:common`, `:core:domain`,
  `:core:data`, `:features:test-fixtures`) or `id("pharmacy.kmp.compose.library")`
  (compose-aware — `:core:ui`, `:features:shared`, all 20 `:features:<x>`).
  The compose flavor inherits the base and additionally applies compose plugins +
  common compose deps + `compose.resources` defaults. `:composeApp` applies
  `pharmacy.architecture.audit` for the `auditArchitecture` task.
- **Test verify** (full sweep — used in this commit's verify):
  ```bash
  ./gradlew :composeApp:auditArchitecture \
            :composeApp:testDebugUnitTest \
            :composeApp:compileTestKotlinIosSimulatorArm64 \
            :composeApp:compileTestKotlinWasmJs \
            :features:auth:jvmTest :features:bulkimport:jvmTest \
            :features:customers:jvmTest :features:expiry:jvmTest \
            :features:help:jvmTest :features:imports:jvmTest \
            :features:ky:jvmTest :features:labels:jvmTest \
            :features:movements:jvmTest :features:offlinesync:jvmTest \
            :features:planning:jvmTest :features:profile:jvmTest \
            :features:reports:jvmTest :features:saleshistory:jvmTest \
            :features:sell:jvmTest :features:settings:jvmTest \
            :features:stock:jvmTest :features:stockcount:jvmTest \
            :features:suppliers:jvmTest :features:users:jvmTest \
            :core:common:jvmTest :core:domain:jvmTest \
            :core:ui:jvmTest :core:data:jvmTest
  ```
  Quick smoke (runs the full dependent tree): `./gradlew :composeApp:check`.
  Project test count today: ~460 `@Test` functions across 65 commonTest files
  (most concentrated in the 20 per-feature modules). Re-measure with
  `grep -rn '@Test' core features composeApp --include='*.kt' | wc -l`.
- **Design system**: tokens in `:core:ui` →
  `ui/theme/DesignTokens.kt`; primitives in
  `ui/designsystem/Pharm*.kt`
- **No M3 widgets** in net-new files: use `PharmButton` / `PharmBadge` /
  `PharmTextField` / `FormField` / `PharmModal` / `MetricCard` / `DrugCard` /
  `PharmTable` / `PharmFilterChips` / `PharmIcons` (SVG vectors)
- **DTO field convention (camelCase Kotlin + explicit `@SerialName`)**: in
  `:core:data/.../data/remote/dto/` and `:core:data/.../data/storage/*Dto.kt`,
  every `@Serializable data class` property must:
  1. Use **camelCase** Kotlin field names — `val sellPrice: Double`, never `val sell_price: Double` (A25)
  2. Carry an explicit `@SerialName("wire_name")` — even when wire name matches Kotlin name (A24)
  Single-line format: `@SerialName("sell_price") val sellPrice: Double = 0.0,`. Both
  rules enforced by `auditArchitecture` Gradle task (in `build-logic/`).
- **Typed errors (A28)**: production code throws typed `AppException`
  subclasses, never raw `IllegalStateException` / `RuntimeException` /
  `Exception`. `BaseUseCase.invoke()` wraps once via `runCatching` and
  converts to `Result<R>` at the use case layer. Repositories return bare
  `T` and throw typed exceptions; the `:features:test-fixtures` module is
  exempted (fakes throw `RuntimeException("...")` as a deliberate test
  signal). Use Storage / UnsupportedPlatform subclasses for IO/platform
  failures in `:composeApp/<plat>Main/platform/`.

- **Adding a new feature** → follow the 6-step recipe above (see
  `MODULE_GRAPH.md` for full graph + adding-new-code quick-lookup table).

## Cross-cutting reminders

See repo-root `CLAUDE.md` at `/Users/admin/ProjectPos/CLAUDE.md` for
workspace-wide conventions (auth flow, role hierarchy, Thai-first copy,
typo-preserving package names in Go services, etc.).
