---
name: kmp-rules
description: Cross-cutting coding conventions of the pharmacy app — no-comments, immutability, naming, null-safety, file size, value classes, determinism, localized copy. Use as the single source for "what makes our code OUR code", and as the first review checklist.
---

# kmp-rules

The conventions every line obeys. Net-new code is held to the full bar; legacy
code is brought up to it when you touch it.

## 1. No comments

No `//`, no `/* */`, no KDoc, no TODO / FIXME / NOTE / HACK, no file-header
banners — anywhere in `.kt`, including tests, fakes and `:features:test-fixtures`.
Code documents itself through:

- descriptive names (rename, don't annotate)
- small focused functions (split when the body needs a section banner)
- types (a `Customer` parameter doesn't need explaining)
- tests (the `@Test` name is the documentation)

Exceptions, because they are annotations rather than prose: `@Suppress("…")`,
`@OptIn(…)`, and license headers required by an upstream library (none today).

When editing code that has comments, strip them as part of the edit.

## 2. Immutability

- `val` by default; `var` only where mutation is genuinely required.
- `data class` for value types; `List` / `Map` / `Set` in public APIs.
- State changes are copy-on-write: `setState { copy(field = value) }`.
- Never mutate a parameter, and never hand a `MutableList` across a layer
  boundary — return a new list.

## 3. Naming

- `camelCase` functions/properties, `PascalCase` types, `UPPER_SNAKE_CASE` for
  `const val`.
- Booleans start with `is` / `has` / `should` / `can` (`isLoading`,
  `canSubmit`, `hasUnsavedChanges`).
- Interfaces name the behaviour — `Clickable`, never `IClickable`.
- Suffix conventions: `…ViewModel`, `…UiState`, `…Callbacks`, `…UiStateError`,
  `…Repository`, `…UseCase`, `…Dto`, `…Api`, `…RepositoryImpl`, `…Nav`.
- Design-system primitives are `Pharm…`; the file name is the primary
  declaration.

## 4. Null safety — no `!!`

There is currently **zero `!!` in production code**. Keep it that way: `?.`,
`?: default`, `requireNotNull(value) { "context" }`, `?.let { … }` (max two
levels of nesting).

## 5. File size and focus

- Typical file 200–400 lines; **hard cap 800**. Nothing in the repo exceeds
  800 today, and only 8 files exceed 400 — the largest is `PharmSidebar.kt` at
  748.
- One concept per file: Screen / Content / ViewModel / UiState / Callbacks each
  live alone (`kmp-code-pattern`).
- Functions under ~50 lines; nesting depth ≤ 4; prefer early returns.

## 6. No magic numbers, no magic strings

- Sizing comes from `pharmTokens` (`dimens.controlHeight`, `spacing.s4`,
  `shapes.lg`), not sprinkled `.dp` literals. If a value is new, add the token.
- Thresholds, delays and limits are `const val` at file top.
- Route names, storage keys and protocol constants are `const val` — never
  repeated literals.

## 7. Value classes for money and quantity

Every monetary field on a domain model or param is `Money`; every counted-stock
field is `Quantity` (`:core:common/value/`).

- Defaults are `Money.Zero` / `Quantity.Zero`, never `0.0` / `0`.
- Predicates are `isPositive` / `isZero`, not `> 0` / `== 0.0`.
- Aggregate in Money space:
  `items.fold(Money.Zero) { acc, x -> acc + x.lineTotal }`, not
  `sumOf { it.lineTotal.amount }`.
- Unwrap only at the boundary: `.amount` / `.value` at the display call site or
  in a mapper. DTOs stay `Double` / `Int`.
- Form fields stay `String` while editing and are wrapped at submit.
- Deliberately still `Double`: `ReportSummary`, `DailySales`, `MonthlySales`,
  and receipt-template wire fields.

## 8. Determinism

In production code (outside `:composeApp/<plat>Main` and tests), avoid hidden
sources of nondeterminism: take a timestamp or clock as a parameter rather than
reading the wall clock, and thread a seeded source rather than calling
`Random` inline. The one accepted exception is
`domain/extension/RequestIdExt.kt`, where `newClientRequestId()` deliberately
generates a random idempotency key for the offline sale queue.

## 9. Copy is localized, never literal

User-facing text comes from `pharmStrings` — the Kotlin-typed table in
`:core:ui/i18n/`. Default is Thai, English switches live.

- A29 fails the build on a Thai literal in production UI code. **English
  literals are not caught** — they are still wrong, and reviewers must flag
  them.
- Adding copy means adding the key to the group interface **and** both the `Th`
  and `En` objects. There is no other supported path.
- Identifiers, log strings, error `message` keys and protocol constants stay
  English and are not localized.
- `remember {}` / `semantics {}` / `LaunchedEffect` bodies can't call
  `pharmStrings` — capture `val s = pharmStrings` at composable scope and key
  caches with it (`remember(s) { … }`).
- Enum display labels are `label(s)` extension functions, never a
  `label: String` field.
- Legitimately still Thai: `PharmStringsTh` itself, `@Preview` sample data,
  `.contains(…)` matching tokens, stored-data defaults, printed receipts
  (`ui/print/`), bulk-import example JSON, and KY official form copy.

## 10. Imports and dependency direction

- Never import across feature boundaries; never import `:core:data` from a
  feature (A20). Both are build-enforced.
- No wildcard imports.
- Import order is whatever the IDE settles on — nobody hand-sorts.

## 11. Tests are code

Same rules: no comments, immutable fixtures, descriptive `@Test` names. A
sloppy test is an unmaintained test, which is a false safety net.

## What the build actually checks

`auditArchitecture` enforces A10 / A17 / A19 / A20 / A23–A29 — layering, DTO
conventions, platform folders, no `expect`, typed errors, no Thai literals.
It does **not** check comments, `!!`, file length, magic numbers or M3 leakage.
Those are review-time; see `kmp-review` for severities and `kmp-build-logic`
for how the task is wired.
