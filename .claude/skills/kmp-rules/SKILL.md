---
name: kmp-rules
description: Cross-cutting coding conventions for a Compose Multiplatform project — no-comments, immutability, naming, null-safety, file size, side-effect bans, copy language. Use as the single source for "what makes our code OUR code", and as the first review checklist.
---

# kmp-rules

The conventions every line of code in the project obeys. Each rule is short, opinionated, and
enforceable by a `grep` or a Gradle audit task. **Net-new code is held to the full bar; legacy
code is brought up to the bar when you touch it.**

## 1. No comments

**No `//`, no `/* */`, no KDoc, no TODO/FIXME/NOTE/HACK, no file header banners.** Anywhere in
`.kt` — production, tests, fakes, fixtures. Code is self-documenting via:

- descriptive names (rename the function/local, don't comment it)
- small focused functions (split when the body needs a section banner)
- types (a `Customer` argument doesn't need `// the customer to add`)
- tests (test names document the behavior)

**Acceptable exceptions** (annotation, not comment):
- `@Suppress("…")` annotations
- tool/compiler directives (`//go:build`, `// eslint-disable-…`, `// @ts-expect-error`, Kotlin
  `@OptIn(…)`, Dart `// ignore_for_file:`)
- license headers required by upstream libraries (none in this project today)

When editing legacy code with comments, **strip them as part of the edit**.

## 2. Immutability

- Prefer `val` over `var`. Use `var` only when mutation is genuinely required (loop counters, etc.).
- `data class` for value types; immutable collections (`List`, `Map`, `Set`) in public APIs.
- State updates are copy-on-write: `state.copy(field = newValue)` via `setState { copy(...) }`.
- **Never** mutate a parameter; never call `.add`/`.remove` on a `MutableList` exposed from a
  shared layer (return a new list).

## 3. Naming

Follow Kotlin conventions; deviations require a CLAUDE.md note:

- `camelCase` — functions, properties, locals.
- `PascalCase` — classes, interfaces, objects, type aliases.
- `UPPER_SNAKE_CASE` — `const val` or `@JvmStatic` true constants.
- **Booleans** start with `is` / `has` / `should` / `can` (`isLoading`, `hasError`, `canSubmit`).
- **Interfaces** name the behavior, not "I"-prefixed: `Clickable`, not `IClickable`.
- **ViewModels** end in `ViewModel`, **States** end in `UiState`, **Callbacks** end in `Callbacks`.
- File name == primary class/function. `<X>ViewModel.kt` contains `<X>ViewModel` and nothing
  else of equal weight.

## 4. Null safety — no `!!`

**No `!!` operator in production.** Use:
- `?.` for safe navigation
- `?: default` for fallback values
- `requireNotNull(value) { "context message" }` when you guarantee non-null and want a typed
  failure
- `?.let { … }` for scoped null-safe operations (max 2 levels of nesting)

The one acceptable `!!` is on `KClass.qualifiedName` inside the navigation `routeKey` helper
where the qualified name is a compile-time invariant of the route type — and even that should
move to `requireNotNull(...) { "..." }` if rewritten.

## 5. File size + focus

- Typical file: **200–400 lines**. Hard cap: **800 lines**.
- One concept per file (see the file-per-class rule in **kmp-code-pattern**: Screen / Content /
  ViewModel / UiState each in its own file).
- Function size: small, ideally **< 50 lines**. Split larger functions into named pieces with
  clear responsibilities.
- Nesting depth: **≤ 4**. Prefer early returns over stacked `if`s.

## 6. No magic numbers / no magic strings

- Use named constants (`const val` at file top, or token values) for thresholds, delays, limits,
  paddings. `dimens.controlHeight` not `40.dp` sprinkled around the codebase.
- Error messages and labels can be inline string literals (user-facing copy) — but identifiers,
  keys, route names, and protocol constants must be `const val`.

## 7. Determinism / side-effects bans

Forbidden in production code (`.kt` outside `:composeApp/<plat>Main` and tests):
- `Date.now()` / `System.currentTimeMillis()` — pass a `Clock` or timestamp through arguments.
- `Math.random()` / `kotlin.random.Random()` without a passed-in seed — pass a `Random` source.
- Reading environment variables at top-level — read them once at startup in `:composeApp`.

These rules let workflows (test, replay, resume) cache or re-run code deterministically.

## 8. Copy language

User-facing strings respect the project's primary language. For the Thai-first project:
- All UI copy, error messages from VMs, and toast text are **Thai**.
- Identifier/constant/log strings remain English.
- `lang="th"` set on root layouts (web) / `Locale` configured at app startup (mobile/desktop).

For a project with a different primary language, swap "Thai" for that language and keep this
rule.

## 9. Imports + dependency direction

- `import` order: standard, third-party, project — separated by blank lines (the IDE settles
  this).
- **Never** import across feature boundaries — `:features:<x>` importing `:features:<y>` is a
  compile error (see **kmp-code-pattern**).
- **Never** import `:core:data` from a `:features:*` module.
- Wildcard imports (`import foo.bar.*`) discouraged; let the IDE expand.

## 10. Tests are code

The same rules apply: no comments, immutable test data, descriptive `@Test` names. Tests don't
get a free pass to be sloppy.

Sloppy test = unmaintained test = false-confidence safety net.

## How to enforce

A Gradle audit task should grep for the testable subset:
- `\s//(?!\s*(go:|eslint-|@ts-|noinspection)|\s/\*[^*]` → comment in `.kt`
- `!!` outside known-safe patterns → fail
- `Date\.now\(|System\.currentTimeMillis\(|Math\.random\(|Random\.nextInt\(\)` in production → fail
- a `.kt` file > 800 lines → fail
- two or more of `class .*ViewModel|fun .*Screen\(|fun .*Content\(|class .*UiState` in the same file → fail

See **kmp-build-logic** for how to wire that as an `auditArchitecture` task, and **kmp-review**
for severity classification.
