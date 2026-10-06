---
name: kmp-review
description: Review a diff in the pharmacy app against its module boundaries, the 11 build-enforced audit rules, MVVM contracts, design-system + layout patterns, i18n and test coverage. Use when reviewing a PR or auditing a change.
---

# kmp-review

Review a diff against the rules that actually exist here. Start from
`git diff origin/develop...HEAD`, classify findings
`[CRITICAL] / [HIGH] / [MEDIUM] / [LOW]`, cite `file:line`, and run the verify
sweep rather than eyeballing.

Companions when in doubt: `kmp-code-pattern` (layering + MVVM),
`kmp-design-system` (tokens + primitives), `kmp-layout-pattern` (page
structure), `kmp-error-handling` (typed errors).

## P0 — dependency boundaries

| Module | May depend on |
|---|---|
| `:core:common` | kotlinx only — **zero project deps** |
| `:core:domain` | `:core:common` |
| `:core:ui` / `:core:data` | `:core:common` + `:core:domain` |
| `:features:test-fixtures` | `:core:common` + `:core:domain` |
| `:features:<x>` | `:core:domain` + `:core:ui` — **no sibling feature, no `:core:data`** |
| `:composeApp` | everything; the only module with platform folders |

A sibling-feature import, a feature reaching `:core:data`, or a `:core:*`
module importing `presentation.*` → `[CRITICAL]`.

## P0 — the 11 build-enforced rules

`./gradlew :composeApp:auditArchitecture` (also runs as part of `check`):

| Rule | Fails on |
|---|---|
| A10 | `core/**` importing `app.devper.pharm.presentation.*` |
| A17 | any import of the removed `domain.common.*` package |
| A19 | imports of stale pre-split `:core:ui` packages |
| A20 | `features/**` importing `app.devper.pharm.data.*` |
| A23 | a feature `di/*Module.kt` importing `domain.usecase` / `observer` / `parser` |
| A24 | a `@Serializable` DTO property without `@SerialName` |
| A25 | a DTO property with a snake_case Kotlin name |
| A26 | a platform source folder outside `:composeApp` |
| A27 | any `expect` declaration |
| A28 | `throw`/`Result.failure(` of a generic exception in production |
| A29 | a Thai string literal in production UI code |

Tripping one → `[CRITICAL]`. If a diff is red on the audit, stop there.

**Not enforced by the build** — so these are the ones a reviewer actually has
to catch: raw Material 3 usage, hex colors outside `theme/`, the file-per-class
rule, comments, missing tests, missing `ReloadOnResume`, and every layout
convention below.

## P1 — code pattern

- **No comments** in any `.kt`, including KDoc / TODO / FIXME / section
  banners. A diff that adds one → `[HIGH]`; fix by renaming or splitting.
  Strip surrounding comments when editing legacy code.
- **MVVM**: Composables talk to ViewModels only — no `koinInject()` of a use
  case or repository in a `@Composable`. ViewModels take use cases, never
  repositories, APIs or `:core:data`.
- **Errors are typed end-to-end**: the UiState carries
  `errorState: AppException?` (never `error: String?`), the VM never localizes,
  the Content calls `localize<X>(pharmStrings)` at render. Repos return bare
  `T` and throw typed; `BaseUseCase` wraps once. `runCatching` added inside a
  repository impl or a use case `execute()` → `[HIGH]`.
- **State**: immutable `data class` implementing `BaseUiState` /
  `LoadableUiState` / `BaseFormUiState`; mutations via `setState { copy(…) }`;
  async via `launchResult(...)`; forms extend `BaseFormViewModel` and gate on
  `canSubmit`.
- **State collection**: `collectAsStateWithLifecycle()`, never
  `collectAsState()` → `[HIGH]`.
- **Money / Quantity**: monetary fields are `Money`, counted stock is
  `Quantity`, unwrapped only at the display call site (`fmtBaht(x.amount)`).
  A new `Double` price on a domain model → `[MEDIUM]`.
- **Routes**: a feature declaring `@Serializable` routes must apply
  `alias(libs.plugins.kotlin.serialization)`; missing it compiles green and
  crashes at runtime → `[HIGH]`. A new destination missing from `DEST_INFO`,
  or a new sub-page missing from `SUB_PAGE_ROUTE_KEYS` → `[HIGH]`.

## P1 — design system + layout

- Net-new UI uses `Pharm*` primitives and `pharmTokens`. Raw M3
  `Button` / `OutlinedTextField` / `Card` / `Scaffold` / `TopAppBar` /
  `FilterChip` / `AlertDialog` / `HorizontalDivider` → `[HIGH]` each. `Icon`,
  `Text` (with a `PharmText` style) and `Surface` are fine.
- Hardcoded `Color(0xFF…)` outside `theme/`, or a raw `Dp` where a
  `PharmDimens` field exists → `[HIGH]`.
- **List pages use `PharmListScaffold`**; a hand-rolled
  `Column { toolbar ; surface card }` → `[HIGH]`.
- **Four list states in order**: skeleton → error → empty → data. A list that
  branches straight from skeleton to empty renders a failed load as "no
  records" → `[HIGH]`.
- **Sub-pages**: `Column { PharmListToolbar(onBack) ; weight(1f) content }`.
  Flag a self-built back header, a bottom save bar or inline Cancel, and
  `fillMaxSize()` on the content column → `[HIGH]` each.
- **Gutters and width**: `pharmPageGutter` / `pharmFormContentPadding()` /
  `pharmFormContentWidth()` rather than hardcoded horizontal padding →
  `[MEDIUM]`.
- **Responsive tiers**: `isCompactShell` (< 840, chrome) vs `isCompactContent`
  (< 600, density). A raw `windowSize == WindowSize.Compact` comparison →
  `[MEDIUM]`. New layouts must survive 320dp (the web floor).
- **`ReloadOnResume(vm::reload)`** on every list/dashboard `Screen.kt` —
  missing it means stale data after an add/edit → `[MEDIUM]`.
- `PharmTable` is already responsive; a fixed grid that bypasses it →
  `[MEDIUM]`.

## P1 — i18n

- Every user-visible string comes from `pharmStrings`. A29 catches Thai
  literals; **English literals are not caught by the build** — flag them
  manually → `[HIGH]`.
- A new key must be added to the group interface **and** both `Th` and `En`
  objects. A key present in `Th` but not `En` won't compile, but a key added to
  the wrong group is a real review finding.
- `remember {}` / `semantics {}` / `LaunchedEffect` bodies can't call
  `pharmStrings` — expect `val s = pharmStrings` captured at composable scope,
  and caches keyed on it (`remember(s) { … }`) so tables rebuild on locale
  switch. A `remember { columns() }` that ignores `s` → `[MEDIUM]` (stale copy
  after switching language).
- Enum display labels are `label(s)` extension functions, never a
  `label: String` field on the enum.

## P1 — data layer

- Mappers live in `core/data/…/repository/internal/<X>Mapper.kt` as `internal
  fun` extensions — inlined mapping in the impl → `[MEDIUM]`.
- An Api hardcoding a host instead of taking `ApiConfig` → `[HIGH]`.
- A DTO type imported into `:core:domain` or `:features:*` → `[CRITICAL]`.

## P1 — files and tests

- A file containing two of {Screen, Content, ViewModel, UiState} → `[HIGH]`
  (not build-enforced despite what older notes claimed).
- `<X>Content.kt` without `@Preview` variants (loaded / loading / empty) →
  `[MEDIUM]`.
- A new ViewModel without `<X>ViewModelTest.kt` → `[MEDIUM]`.
- A new parser / validator / pricing rule without a `:core:domain` unit test →
  `[MEDIUM]`.
- A PR that raises coverage without raising `COVERAGE_FLOOR` → `[LOW]`.

## P2 — polish

- Accessibility: `selectableGroup()` around radio chips, meaningful
  `contentDescription` / `Role`, `liveRegion` + `mergeDescendants` on alert
  banners.
- No silent truncation or caps without surfacing them.
- Reduced-motion respected via `LocalReducedMotion` on new animations.

## Recognise "looks-wrong-but-is-right"

- `BaseUseCase` lives in `:core:common` under package
  `app.devper.pharm.domain.usecase` — deliberate split package.
- Repositories return bare `T` with no `runCatching`.
- `expectSuccess = false` on the shared `HttpClient` — the explicit
  `HttpResponseValidator` does the translating.
- Use cases take `AppDispatchers`; ViewModels do not.
- `:features:test-fixtures` fakes throw generic exceptions — A28-exempt.
- Route files sit in a `navigation/` folder but declare
  `package …presentation.<feat>` — `DEST_INFO` keys on the qualified name.
- Thai literals survive in `PharmStringsTh`, `@Preview` sample blocks,
  `ui/print/`, `.contains(…)` tokens and stored-data defaults.

## Verify before approving

```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :core:{common,domain,ui,data}:jvmTest \
          :features:<changed>:jvmTest \
          koverVerify
```

For anything UI-visible, also **run it** — the `run` skill builds the wasm
bundle, serves it with the mock API and drives it in headless Edge. Code review
and type checks verify code, not what the user sees; every recent P1 in this
repo was found by looking at a screenshot, not by reading a diff.

A green sweep is a precondition for `[APPROVE]`, not a substitute for the
checks above.
