---
name: kmp-review
description: Review a change in a Compose Multiplatform Clean-Architecture project against its module boundaries, audit rules, MVVM contracts, design-system + layout patterns, and test coverage. Use when reviewing a diff/PR or auditing code in a KMP project.
---

# kmp-review

Audit a diff against the rules that actually exist in the project. Start from `git diff` (or the
PR diff), classify findings `[CRITICAL] / [HIGH] / [MEDIUM] / [LOW]`, cite file:line, and prefer
running the verify command over eyeballing.

Read these companion skills first when in doubt:
- **kmp-code-pattern** — module layering + MVVM contracts + DI rules
- **kmp-design-system** — token layer + branded primitives, "no raw M3"
- **kmp-layout-pattern** — the one unified page structure

## P0 — dependency boundaries (Kotlin compile errors)

- `:core:common` → **zero** project deps (kotlinx + koin-core only).
- `:core:domain` → `:core:common` only.
- `:core:ui` / `:core:data` → `:core:domain` + `:core:common`.
- `:features:test-fixtures` → `:core:common` + `:core:domain` + kotlinx.
- `:features:<x>` → `:core:domain` + `:core:ui`. **No sibling `:features:<y>`** (hoist a
  cross-feature `() -> Unit` callback to `:composeApp`). **No `:core:data`** access.
- `:composeApp` → everything (and is the only module with platform folders + `:core:data` access).

A diff that introduces a sibling-feature import, a feature touching `:core:data`, or a `:core:*`
module touching `presentation.*` → `[CRITICAL]`.

## P0 — build-enforced audit rules

These should run as a Gradle task (`auditArchitecture` or similar) and fail the build:

| Rule | Violation |
|---|---|
| Inward-only layering | `:core:*` importing `presentation.*` (a feature package) |
| Cross-feature production import | `:features:<x>` importing from `:features:<y>` |
| Features → data | `:features:<x>` importing from `:core:data` — use the `:core:domain` repository **interface** |
| DI purity | `:features:<x>/di/<X>Module.kt` importing a non-VM type (only `factoryOf(::…ViewModel)` allowed) |
| DTO `@SerialName` | a `@Serializable` DTO property missing an explicit `@SerialName(...)` |
| DTO naming | a DTO property using a snake_case Kotlin name (must be camelCase + `@SerialName`) |
| Platform-folder ownership | `androidMain`/`iosMain`/`jvmMain`/`wasmJsMain` folder outside `:composeApp` |
| No `expect`/`actual` | any `expect` declaration anywhere — use an interface in `:core:common` + impl in `:composeApp/<plat>Main` |
| Typed errors | throwing generic `Exception`/`RuntimeException`/`IllegalStateException` in production (`:features:test-fixtures` exempt) |
| File-per-class | a file containing 2+ of {Screen, Content, ViewModel, UiState} |

Run it: `./gradlew :composeApp:auditArchitecture`. A diff that trips one → `[CRITICAL]`.

## P1 — conventions (manual review)

### Code pattern
- **No comments** in any `.kt` (incl. KDoc/TODO/FIXME). A diff that adds a comment → `[HIGH]`,
  fix by renaming/refactoring. Strip surrounding comments when editing legacy code.
- **MVVM**: Composables talk to ViewModels only — never `koinInject()` a UseCase/Repository in a
  `@Composable`. VMs depend on **use cases** (+ `Provider`s), never repos/APIs/`:core:data`.
- **Errors**: VMs surface `state.error: String?`; every screen renders `ErrorBottomSheet`. Repos
  return **bare `T`** and throw typed `AppException`; `BaseUseCase` wraps once in `runCatching`.
  Do **not** add `runCatching` inside repository impls.
- **State**: `*UiState` is an immutable `data class` implementing `BaseUiState`; VMs use
  `setState { copy(...) }` + `launchResult(...)`; forms use `BaseFormViewModel` + `canSubmit`.
- **State collection**: `collectAsStateWithLifecycle()`, never `collectAsState()` (battery).
- **Routes**: a feature module declaring `@Serializable` route objects must apply
  `kotlin.serialization` plugin in its `build.gradle.kts` — otherwise the serializer is missing
  at runtime even though the build is green. `[HIGH]` if missing.

### Design system + layout
- **Design system**: net-new UI uses `Brand*` primitives + `brandTokens` — **no raw Material 3
  widgets** (`Button`/`OutlinedTextField`/`Card`/`Scaffold`/`TopAppBar`/`FilterChip`/etc.), no
  hardcoded colors (use tokens), no emoji-as-icons (use the brand icon set). `[HIGH]` per
  violation in net-new code.
- **Layout (single pattern)**: every page is `Column { BrandListToolbar ; weighted content
  column }`. Sub-pages just pass `onBack` to the toolbar. Specifically flag:
  - A net-new sub-page building its own back-header instead of `BrandListToolbar(onBack=…)` → `[HIGH]`.
  - A form shipping a bottom save bar or inline Cancel button (back arrow is the way out) → `[HIGH]`.
  - A content column using `fillMaxSize` instead of `weight(1f)` (overlaps the toolbar) → `[HIGH]`.
  - A list toolbar nested inside the table surface card (the old style) → `[MEDIUM]`.
  - A form centering itself with `widthIn(max = …)` while list pages are full-width → `[MEDIUM]`.
- **Forms**: `FormField` static-label pattern, pin single-line fields to `height(56.dp)`, reserve
  conditional `trailingIcon` slots. Wrap each section in `BrandFormCard(title)`. Save lives in
  the toolbar `actions` slot via `BrandSaveAction(saving, canSubmit, onSubmit)`.
- **List resume reload**: every list/dashboard `Screen.kt` calls `ReloadOnResume(vm::reload)`
  (or `applyFilter` / `loadList`) so a record added on a detail page reflects on return.
  Missing this on a list screen → `[MEDIUM]` (stale UI after add/edit).

### Data layer
- **DTO mapping**: domain `*Param` → wire `*Request` via `private fun *Param.toRequest()` at file
  bottom in `:core:data`; domain never sees DTOs.
- Repository impl with `runCatching`/`try-catch` swallowing errors → `[HIGH]` (let typed
  `AppException` propagate; use cases wrap once).

### File-per-class
- `<X>Screen.kt` containing both the VM and the UiState → `[CRITICAL]` (audit-enforced).
- `<X>Content.kt` lacking `@Preview` variants → `[MEDIUM]`. Loaded + Loading + Empty at minimum.

### Tests
- A new VM without a `<X>ViewModelTest.kt` → `[MEDIUM]`. (Help-style VMs with no injectable
  deps and parsing covered separately are the only acceptable exception.)
- A new domain operation (parser/pricing/validator) without a unit test → `[MEDIUM]`.

## P1 — responsive

- Respect breakpoints 320 (floor) / 360 / 600 (Compact↔Medium) / 720 (metric 4-up) / 840
  (Medium↔Expanded). New full-width layouts should survive 320dp and not assume `<600dp` on
  desktop/web.
- Prefer `BoxWithConstraints` + `FlowRow` over fixed `Row`s that crush weighted children on
  narrow screens.
- `BrandTable` is already responsive (card mode `<600dp`, horizontal scroll) — flag fixed-grid
  tables that bypass it.

## P2 — polish

- Accessibility: `selectableGroup()` around radio chips; meaningful `contentDescription`/`Role`;
  `liveRegion` + `mergeDescendants` on alert banners.
- No silent caps/truncation without surfacing it.
- User-facing strings respect the project's language convention (e.g. Thai-first).

## Recognise "looks-wrong-but-is-right"

- `BaseUseCase` may live in `:core:common` under package `<base>.domain.usecase` (split package
  — intentional, so domain can `api(:core:common)` it).
- Repositories return bare `T` (no `Result<T>`) and have no `runCatching` — intentional.
- Use cases take `AppDispatchers`; ViewModels do **not** (they use `viewModelScope`).
- Test-fixture fakes throw `RuntimeException` — deliberate test signal, audit-exempt.

## Verify before approving

```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :core:common:jvmTest :core:domain:jvmTest :core:ui:jvmTest :core:data:jvmTest
# plus :features:<changed>:jvmTest for touched feature modules
```

A green canonical sweep is a necessary precondition for `[APPROVE]` — not a substitute for the
manual checks above.
