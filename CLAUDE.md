# CLAUDE.md — pharmacy-app KMP companion

Project-scoped instructions for Claude Code when working in
`/Users/admin/ProjectPos/pharmacy-app/app-kmp/`. Applies to the KMP
companion app (Compose Multiplatform — 6-module Gradle layout: `:composeApp`
+ `:core:common` + `:core:domain` + `:core:ui` + `:core:data` + `:features`).

## Module structure

```
:composeApp                              entry point + ONLY module with platform source folders
  ├─ App.kt + AppViewModel + AppNavHost
  ├─ MainActivity / MainViewController / Main (jvm/wasm)
  ├─ di/AppModule.kt                     composition root — includes(commonModule, domainModule, dataModule, + 10 feature modules)
  ├─ <plat>Main/platform/PdfDownloaderImpl.kt × 4   platform impls of PdfDownloader interface
  ├─ <plat>Main/platform/ReceiptPrinterImpl.kt × 4  platform impls of ReceiptPrinter interface
  └─ <plat>Main bindings: each Main*.kt's platform Koin module binds Settings + HttpClient engine +
        AppDispatchers (platform-appropriate IO) + PdfDownloaderImpl + ReceiptPrinterImpl
  deps: :features + :core:ui + :core:data + :core:domain + :core:common

:core:common                             interfaces + pure infra (commonMain + commonTest only)
  ├─ common/ AppDispatchers (data class only, no .real() factory)
  ├─ common/ Logger, PrintlnLogger, AppException
  ├─ common/print/ ReceiptTemplate + ReceiptTemplate.kt (data) + ReceiptPrinter.kt (interface)
  ├─ common/platform/ PdfDownloader.kt (interface)
  ├─ common/di/CommonModule.kt           (Koin module — binds Logger only)
  ├─ domain/usecase/ BaseUseCase + BaseSyncUseCase
  package: app.devper.pharm.common (+ app.devper.pharm.domain.usecase for BaseUseCase)
  deps: kotlinx only (zero project deps) + koin-core
  rule: NO platform source folders (POST-INVERT). Every platform-bound concern is an
        interface here; impls live in :composeApp/<plat>Main and are Koin-bound per platform.

:core:domain                             pure domain — commonMain + commonTest only
  ├─ model/ param/ repository/ usecase/
  ├─ parser/ util/ pricing/ event/ observer/
  ├─ di/DomainModule.kt                  (Koin module — 76 bindings:
  │                                        5 providers + 1 parser + 68 use cases +
  │                                        2 settings UseCases)
  deps: :core:common (+ kotlinx + koin-core)
  rule: ไม่มี androidMain / iosMain / jvmMain / wasmJsMain folder
  note: api(:core:common) — re-exports so :features เห็น :core:common ทาง transitively

:core:ui                                 shared compose infra (commonMain + commonTest only)
  ├─ ui/theme/ designsystem/ common/ components/
  ├─ ui/format/ scanner/ print/ help/        (print/ holds ReceiptBuilder only —
  │                                           ReceiptTemplate + expect printReceipt
  │                                           live in :core:common)
  ├─ composeResources/font/sarabun_*.ttf
  packages: app.devper.pharm.ui.*
  packageOfResClass = "app.devper.pharm.ui.resources"
  deps: compose + :core:common + :core:domain
  rule: ไม่มี androidMain / iosMain / jvmMain / wasmJsMain folder
        (ทุก expect/actual ลง :core:common เท่านั้น)

:core:data                               repository impls + transport (commonMain + commonTest only)
  ├─ data/network/ storage/ repository/
  ├─ data/remote/api/ remote/dto/
  ├─ data/di/DataModule.kt               (33 bindings: Apis + RepositoryImpls)
  deps: ktor + multiplatform-settings + koin-core + :core:common + :core:domain
  rule: ไม่มี androidMain / iosMain / jvmMain / wasmJsMain folder
        (PdfDownloader expect/actual ย้ายไป :core:common)

:features                                ทุก feature รวมที่เดียว
  ├─ presentation/<feature>/             (19 features)
  │    auth, sell, customers, suppliers, stock, stockcount, imports,
  │    bulkimport, movements, expiry, planning, reports, ky, settings,
  │    offlinesync, help, saleshistory
  ├─ di/<Feature>Module.kt               (10 per-feature DI modules —
  │                                        bind ONLY VM `factoryOf`;
  │                                        Use cases / Providers / Parser
  │                                        live in :core:domain's `domainModule`;
  │                                        Repository / Api live in
  │                                        :core:data's `dataModule`)
  ├─ presentation/navigation/ShelledScreen.kt
  ├─ composeResources/files/user_guide.md
  packageOfResClass = "app.devper.pharm.features.resources"
  deps: :core:domain + :core:ui ONLY
        (`:core:common` types accessible transitively via :core:domain's
        api() re-export; :core:data NEVER accessible — features inject
        repository interfaces from :core:domain)
```

**Forbidden (P0 — audited via review skill):**
- `:core:common` ห้ามรู้จัก project module อื่นเลย (zero project deps)
- `:core:domain` ห้ามรู้จัก `:core:ui` / `:core:data` / `:features` / `:composeApp`
- `:core:*` ห้ามรู้จัก `:features` หรือ `:composeApp`
- `:features` ห้ามรู้จัก `:core:data` (\*RepositoryImpl, \*Api, DTO — features
  ใช้ Repository interface จาก :core:domain เท่านั้น; data bindings อยู่ใน
  :core:data/.../di/DataModule.kt ซึ่ง :composeApp include เอง)
- `:features` ห้ามรู้จัก `:composeApp`
- **A26**: เฉพาะ `:composeApp` เท่านั้นที่มี platform source folders
  (`androidMain` / `iosMain` / `jvmMain` / `wasmJsMain`); module อื่นเป็น
  `commonMain` + `commonTest` ล้วน ๆ
- **A27**: ห้ามมี `expect class` / `expect fun` / `expect val` ที่ไหนในโปรเจกต์;
  ใช้ interface ใน `:core:common` + impl ใน `:composeApp/<plat>Main` แทน

Cross-feature import ภายใน `:features` (เช่น `presentation.sell` →
`presentation.customers`) ยังเป็น P1 convention check — เมื่อ split per-feature
ในอนาคตจะกลายเป็น P0.

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
- Production code in `composeApp/src/*`, `core/{domain,ui,data}/src/*`, `features/src/*`
- Test code in `*/src/commonTest`, `*/src/jvmTest`, `*/src/androidUnitTest`
- Fakes / fixtures / helpers in any test source set
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

- Phase-letter notes (M-CC) are now living history in commit messages /
  PR descriptions, not buried in source
- Skill files (`pharmacy-kmp-*` SKILL.md) document patterns at a higher
  level; in-code comments duplicate that
- KMP cross-compiles 5 targets — every byte of source matters for context

## Project reminders (KMP-specific)

- **Stack**: Kotlin Multiplatform 2.3.0 / Compose Multiplatform 1.9.3 / AGP
  8.13.2 / Gradle 8.14.3
- **Targets**: `jvm`, `android`, `iosX64`, `iosArm64`, `iosSimulatorArm64`, `wasmJs`
- **Modules** (6): `:composeApp` (entry), `:core:common` (expect/actual + dispatchers/logger/exceptions/UseCase framework),
  `:core:domain` (pure — commonMain+commonTest only), `:core:ui` (compose infra),
  `:core:data` (repos + transport), `:features` (all 19 features)
- **Convention plugins** (`build-logic/`): each KMP library applies one of
  `id("pharmacy.kmp.library")` (pure data/domain — `:core:common`, `:core:domain`, `:core:data`)
  or `id("pharmacy.kmp.compose.library")` (compose-aware — `:core:ui`, `:features`).
  The compose flavor inherits the base and additionally applies compose plugins +
  common compose deps + `compose.resources` defaults. `:composeApp` applies
  `pharmacy.architecture.audit` for the `auditArchitecture` task.
- **Test verify**: `:composeApp:testDebugUnitTest
  :composeApp:compileTestKotlinIosSimulatorArm64
  :composeApp:compileTestKotlinWasmJs :features:jvmTest :core:domain:jvmTest :core:common:jvmTest`
  (test count today: 205)
- **Design system**: tokens in `:core:ui` →
  `ui/theme/DesignTokens.kt`; primitives in
  `ui/designsystem/Pharm*.kt`
- **No M3 widgets** in net-new files: use `PharmButton` / `PharmBadge` /
  `PharmTextField` / `FormField` / `PharmModal` / `MetricCard` / `DrugCard`
- **DTO field convention (camelCase Kotlin + explicit `@SerialName`)**: in
  `:core:data/.../data/remote/dto/` and `:core:data/.../data/storage/*Dto.kt`,
  every `@Serializable data class` property must:
  1. Use **camelCase** Kotlin field names — `val sellPrice: Double`, never `val sell_price: Double` (A25)
  2. Carry an explicit `@SerialName("wire_name")` — even when wire name matches Kotlin name (A24)
  Single-line format: `@SerialName("sell_price") val sellPrice: Double = 0.0,`. Both
  rules enforced by `auditArchitecture` Gradle task (in `build-logic/`).
- **Adding a new feature** → drop folder under
  `features/src/commonMain/kotlin/app/devper/pharm/presentation/<feature>/`,
  add DI module under `features/.../di/`, wire from `:composeApp`'s `AppModule`
  + `AppNavHost`. See `MODULE_GRAPH.md` for full dep matrix.

## Cross-cutting reminders

See repo-root `CLAUDE.md` at `/Users/admin/ProjectPos/CLAUDE.md` for
workspace-wide conventions (auth flow, role hierarchy, Thai-first copy,
typo-preserving package names in Go services, etc.).
