---
name: pharmacy-kmp-review
description: Review a change in the pharmacy-app KMP companion against its Clean-Architecture boundaries, the 10 build-enforced audit rules, and project conventions (no-comments, typed errors, DTO @SerialName, MVVM, design system, responsive). Use when reviewing a diff/PR or auditing code in /Users/admin/ProjectPos/pharmacy-app/app-kmp.
---

# pharmacy-kmp-review

Audit a diff against the rules that actually exist in this repo. Start from `git diff` (or the PR
diff), classify findings `[CRITICAL] / [HIGH] / [MEDIUM] / [LOW]`, cite file:line, and prefer
running the verify command over eyeballing.

## P0 — the 10 build-enforced rules (`:composeApp:auditArchitecture`)
These are real and fail the build (`build-logic/.../pharmacy.architecture.audit.gradle.kts`).
There is **no A1–A9 / A11–A16** — only these ten:

| Rule | Violation |
|---|---|
| **A10** | `:core:*` importing `app.devper.pharm.presentation` (a `:features:*` package) |
| **A17** | stale `app.devper.pharm.domain.common` imports (post-split package is gone) |
| **A19** | stale `:core:ui` package paths (pre-rename leftovers) |
| **A20** | `:features:*` importing `:core:data` — use the `:core:domain` repository **interface** |
| **A23** | `:features:<x>/di/<X>Module.kt` importing a non-VM type (only `factoryOf(::…ViewModel)` allowed) |
| **A24** | a DTO property missing an explicit `@SerialName(...)` |
| **A25** | a DTO property using a snake_case Kotlin name (must be camelCase + `@SerialName`) |
| **A26** | platform source folder (`androidMain`/`iosMain`/`jvmMain`/`wasmJsMain`) outside `:composeApp` |
| **A27** | any `expect`/`actual` declaration anywhere in the project |
| **A28** | throwing generic `Exception`/`RuntimeException`/`IllegalStateException` in production (`:features:test-fixtures` exempt) |

Run it: `./gradlew :composeApp:auditArchitecture`. If a diff trips one of these, it's `[CRITICAL]`.

## P0 — dependency boundaries (Kotlin compile errors, not just audit)
- `:core:common` → **zero** project deps (kotlinx + koin-core only).
- `:core:domain` → `:core:common` only.
- `:core:ui` / `:core:data` → `:core:domain` + `:core:common`.
- `:features:shared` → `:core:domain` + `:core:ui` only (no `:features:<x>`, no `:core:data`).
- `:features:test-fixtures` → `:core:common` + `:core:domain` + kotlinx.
- `:features:<x>` → `:core:domain` + `:core:ui` + `:features:shared`. **No sibling `:features:<y>`**
  (navigate cross-feature via a `:features:shared` Route object), **no `:core:data`**.
- `:composeApp` → everything (and is the only module with platform folders + `:core:data` access).

## P1 — conventions (manual review)
- **No comments** in any `.kt` (incl. KDoc/TODO/FIXME). A diff that adds a comment → `[HIGH]`,
  fix by renaming/refactoring. Strip surrounding comments when editing legacy code.
- **MVVM**: Composables talk to ViewModels only — never `koinInject()` a UseCase/Repository in a
  `@Composable` (even `AppNavHost` goes through `AppViewModel`). VMs depend on **use cases**
  (+ `Provider`s), never repos/APIs/`:core:data`.
- **Errors**: VMs surface `state.error: String?`; every screen renders `ErrorBottomSheet`. Repos
  return **bare `T`** and throw typed `AppException`; `BaseUseCase` wraps once in `runCatching`.
  Do **not** add `runCatching` inside repository impls (two known domain-layer exceptions only:
  `CheckoutUseCase`, `SubmitKyFormsUseCase`).
- **State**: `*UiState` is an immutable `data class` implementing `BaseUiState`; VMs use
  `setState { copy(...) }` + `launchResult(...)`; forms use `BaseFormViewModel` + `canSubmit`.
- **State collection**: `collectAsStateWithLifecycle()`, never `collectAsState()` (battery).
- **Design system**: net-new UI uses `Pharm*` primitives + `pharmTokens` — **no raw Material 3
  widgets**, no hardcoded colors (use tokens), no emoji-as-icons (use `PharmIcons`).
- **Forms**: `FormField` static-label pattern, pin single-line fields to `height(56.dp)`, reserve
  conditional `trailingIcon` slots.
- **DTO mapping**: domain `*Param` → wire `*Request` via `private fun *Param.toRequest()` at file
  bottom in `:core:data`; domain never sees DTOs.
- **Thai copy** for all user-facing strings.

## P1 — responsive
- Respect breakpoints 320 (floor) / 360 / 600 (Compact↔Medium) / 720 (metric 4-up) / 840
  (Medium↔Expanded). New full-width layouts should survive 320dp and not assume `<600dp` on desktop/web.
- Prefer `BoxWithConstraints` + `FlowRow` over fixed `Row`s that crush weighted children on narrow screens.
- `PharmTable` is already responsive (card mode `<600dp`, horizontal scroll) — flag fixed-grid tables that bypass it.

## P2 — polish
- Accessibility: `selectableGroup()` around radio chips; `liveRegion` + `mergeDescendants` on
  banners (allergy/compliance); meaningful `contentDescription`/`Role`.
- Test coverage: a new VM without a `<X>ViewModelTest.kt` → `[MEDIUM]`.
- No silent caps/truncation without surfacing it.

## Recognise "looks-wrong-but-is-right"
- `BaseUseCase` lives in `:core:common` under package `app.devper.pharm.domain.usecase` (split package — intentional).
- Repositories return bare `T` (no `Result<T>`) and have no `runCatching` — intentional.
- Use cases take `AppDispatchers`; ViewModels do **not** (they use `viewModelScope`).
- Fakes in `:features:test-fixtures` throw `RuntimeException` — deliberate test signal, A28-exempt.

## Verify before approving
```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:testDebugUnitTest \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :core:common:jvmTest :core:domain:jvmTest :core:ui:jvmTest :core:data:jvmTest
# plus :features:<changed>:jvmTest for touched feature modules
```
