---
name: kotlin-coding-style
description: Idiomatic Kotlin coding style conventions covering formatting (ktlint / Detekt / official Kotlin code style), immutability (val over var, data classes, copy-on-write state updates), naming conventions (camelCase / PascalCase / SCREAMING_SNAKE_CASE, no `I` prefix on interfaces), null safety (no `!!`, prefer `?.` / `?:` / `requireNotNull` / `?.let`), sealed types for closed hierarchies with exhaustive `when`, extension function discoverability, scope function selection (let / run / apply / also), and error handling (`Result<T>`, `runCatching`, never catch `CancellationException`, avoid try-catch for control flow). Use when writing or reviewing Kotlin code, refactoring for idiomatic style, setting up linting, choosing between `val`/`var`, picking the right scope function, handling errors, modeling state with sealed types, or working with nullable types. Triggers on "kotlin style", "kotlin idiomatic", "val vs var", "sealed class", "scope function", "let apply run also", "null safety", "ktlint", "detekt", "Result Kotlin", "extension function".
metadata:
  author: worawit
  version: "1.1"
---

# Kotlin Coding Style

Idiomatic Kotlin patterns for writing readable, maintainable, and safe code.

> **Companion**: `~/.claude/rules/kotlin/coding-style.md` is the short always-loaded summary.
> This skill is the long-form reference + per-project deviation notes.

## When to Activate

- Writing or reviewing Kotlin code in any project
- Refactoring code for idiomatic style
- Setting up linting (ktlint, Detekt) or code style enforcement
- Choosing between `val` vs `var`, picking the right scope function, or handling errors
- Modeling state with sealed types

---

## Formatting

- **ktlint or Detekt** for style enforcement (most projects).
- **Official Kotlin code style** (`kotlin.code.style=official` in `gradle.properties`).
- Some opinionated KMP projects skip ktlint and rely on a project-specific Gradle audit task
  instead (see "strict KMP deviations" below) — generic ktlint can conflict with such projects
  (e.g. it'd want to add KDoc which violates a no-comments rule).

---

## Immutability

- Prefer `val` over `var` — default to `val` and only use `var` when mutation is required
- Use `data class` for value types; use immutable collections (`List`, `Map`, `Set`) in public APIs
- Copy-on-write for state updates: `state.copy(field = newValue)`

---

## Naming

Follow Kotlin conventions:

- `camelCase` for functions and properties
- `PascalCase` for classes, interfaces, objects, and type aliases
- `SCREAMING_SNAKE_CASE` for constants (`const val` or `@JvmStatic`)
- Prefix interfaces with behavior, not `I`: `Clickable` not `IClickable`
- Booleans start with `is` / `has` / `should` / `can` (`isLoading`, `hasError`, `canSubmit`)

---

## Null Safety

- Never use `!!` — prefer `?.`, `?:`, `requireNotNull()`, or `checkNotNull()`
- Use `?.let {}` for scoped null-safe operations
- Return nullable types from functions that can legitimately have no result

```kotlin
// BAD
val name = user!!.name

// GOOD
val name = user?.name ?: "Unknown"
val name = requireNotNull(user) { "User must be set before accessing name" }.name
```

---

## Sealed Types

Use sealed classes/interfaces to model closed state hierarchies:

```kotlin
sealed interface UiState<out T> {
    data object Loading : UiState<Nothing>
    data class Success<T>(val data: T) : UiState<T>
    data class Error(val message: String) : UiState<Nothing>
}
```

Always use exhaustive `when` with sealed types — no `else` branch.

---

## Extension Functions

Use extension functions for utility operations, but keep them discoverable:

- Place in a file named after the receiver type (`StringExt.kt`, `FlowExt.kt`)
- Keep scope limited — don't add extensions to `Any` or overly generic types

---

## Scope Functions

Use the right scope function:

- `let` — null check + transform: `user?.let { greet(it) }`
- `run` — compute a result using receiver: `service.run { fetch(config) }`
- `apply` — configure an object: `builder.apply { timeout = 30 }`
- `also` — side effects: `result.also { log(it) }`
- Avoid deep nesting of scope functions (max 2 levels)

---

## Error Handling

- Use `Result<T>` or custom sealed types
- Use `runCatching {}` for wrapping throwable code
- Never catch `CancellationException` — always rethrow it
- Avoid `try-catch` for control flow

```kotlin
// BAD — using exceptions for control flow
val user = try { repository.getUser(id) } catch (e: NotFoundException) { null }

// GOOD — nullable return
val user: User? = repository.findUser(id)
```

---

## Project-specific deviations (strict KMP-style projects)

Opinionated KMP projects often tighten or invert the general patterns above. When a project's
`CLAUDE.md` adopts the **`kmp-*` skill set** (`~/.claude/skills/kmp-rules`,
`kmp-code-pattern`, `kmp-error-handling`, `kmp-data-layer`, `kmp-platform`), follow the
project's rules when they conflict with the general patterns.

| General pattern | Strict-KMP project rule | Why / reference |
|---|---|---|
| KDoc on public classes / functions (`/** ... */`) | **NO comments of any kind in `.kt` files** — no `//`, `/* */`, `/** */` KDoc, no `TODO` / `FIXME` / `NOTE` / `HACK`, no file banners. Self-document via naming + types + small focused functions. `@Suppress` annotations are fine (they're annotations, not comments). | See `~/.claude/skills/kmp-rules/` §1 and `~/.claude/rules/common/coding-style.md`'s "No comments" section. |
| `Repository.getUser(id): Result<User>` — repo returns `Result<T>` and impl wraps in `runCatching` | Repository **interfaces return bare `T`**; impls throw typed `AppException` subclasses. `BaseUseCase` wraps the call once via `runCatching` and converts to `Result<R>` at the use case layer. `runCatching {}` inside a `RepositoryImpl` is a P0 audit violation (you'd be double-wrapping the typed translation). | See `~/.claude/skills/kmp-data-layer/` §4 and `~/.claude/skills/kmp-error-handling/` §2–§3. |
| `throw IllegalStateException("...")` or `Result.failure(RuntimeException(...))` | Every production error uses a typed `AppException` subclass — `AuthException` (401), `ForbiddenException` (403), `NotFoundException` (404), `ConflictException(payload)` (409), `NetworkException` (transport), `ServerException(statusCode, body)` (5xx), `ValidationException` (input), `StorageException`, `UnsupportedPlatformException`. Generic `IllegalStateException` / `RuntimeException` / `Exception` / `UnsupportedOperationException` / `NullPointerException` in production is P0. | See `~/.claude/skills/kmp-error-handling/` §1. Typed errors let VMs branch by `is` checks + Ktor `HttpResponseValidator` translates HTTP status → typed exception automatically + users get consistent user-language messages. |
| `try { ... } catch (e: ApiException) { ... }` / `catch (e: UnauthorizedException) { ... }` | Replace with typed subclass: `AuthException` (401), `ConflictException` (read `e.payload`), `ServerException` (`e.statusCode` / `e.body`), or the sealed parent `AppException`. The `?: "fallback"` pattern in VMs can shrink because the typed exception's `message` is already user-ready copy. | See `~/.claude/skills/kmp-error-handling/` §4 (ViewModel branching). |
| DTO field `val sell_price: Double` (snake_case Kotlin name to match wire) | Kotlin DTO field name must be **camelCase**; wire name stays snake_case via `@SerialName("sell_price") val sellPrice: Double`. Both the annotation AND the camelCase name are audit-enforced. | See `~/.claude/skills/kmp-data-layer/` §3. Kotlin idiom is camelCase; DTO mappers read fluent code (`dto.sellPrice` not `dto.sell_price`); wire shape stays explicit via annotation. |
| `expect class` / `expect fun` / `expect val` for cross-platform contracts | **Banned.** Use a regular Kotlin interface in `:core:common` + per-platform impls in `:composeApp/<plat>Main/platform/` + Koin bindings in each platform's `Main*.kt`. | See `~/.claude/skills/kmp-platform/`. The general kotlin-patterns rule (`~/.claude/rules/kotlin/patterns.md`) lists both options; strict-KMP projects pick Option B. |
| ktlint / Detekt | Often **not configured**. Strict-KMP projects use a custom `auditArchitecture` Gradle task (wired into `:composeApp:check`) that enforces project-specific architecture rules (file-per-class, DTO conventions, layering, no-expect/actual, no generic exceptions, no comments) instead of generic style rules. | See `~/.claude/skills/kmp-build-logic/` §3 for the audit task skeleton and `~/.claude/skills/kmp-review/` for the rule catalog. |
| `data class FooViewModel(...)` taking `dispatchers: AppDispatchers` + `logger: Logger` for testability | VM constructors take ONLY domain dependencies (use cases + `Provider`s + buses). `dispatchers` + `logger` are absent from the VM surface entirely. Use cases own their own IO switch via `BaseUseCase(dispatchers)` + `withContext(dispatchers.io)`. VM body never references `dispatchers.` or `logger.`. | See `~/.claude/skills/kmp-code-pattern/` §3. Cross-cutting infra leaks bloat constructors + make every test rewire boilerplate. The `runVmTest { dispatchers -> }` helper injects the test scheduler into the use case constructor where it actually lives. |
| `class FooViewModel : ViewModel()` + raw `_state = MutableStateFlow(...)` + manual `viewModelScope.launch { try { ... } catch (e) { ... } }` | All VMs extend `BaseViewModel<S>(initial)` and use `setState { copy(...) }` + `launchResult(block = { useCase() }, onSuccess, onFailure)`. Form VMs extend `BaseFormViewModel<S>` over F-bounded `BaseFormUiState<S>` and override `persist(): Result<Unit>` only. | See `~/.claude/skills/kmp-code-pattern/` and `~/.claude/skills/kmp-add-form/`. Eliminates boilerplate; consistent error lifecycle (`error: String?` + `dismissError()`); `launchResult` already handles `runCatching` + `CancellationException` rethrow. |
| `Screen.kt`, `Content.kt`, `ViewModel.kt`, `UiState.kt` may share a file when small | **File-per-class**: each must live in its own file. An audit grep for `class .*ViewModel|fun .*Screen\(|fun .*Content\(|class .*UiState` in the same file fails the build. | See `~/.claude/skills/kmp-code-pattern/` §2. |

### What still aligns with the general patterns

- **Immutability** — `val` everywhere; `data class` ubiquitous; UiStates are immutable copies; `state.copy(field = newValue)` is the only mutation
- **Naming** — camelCase / PascalCase / SCREAMING_SNAKE_CASE / no `I` prefix on interfaces ✓
- **Null safety** — no `!!` in production; `?.let { }` + `?:` everywhere; `requireNotNull` used at boundaries
- **Sealed types** — used extensively (mode enums, status enums, route trees) with exhaustive `when`
- **Extension functions** — file-naming-by-receiver (`MoneyFormat.kt`, `DateFormat.kt`)
- **Scope functions** — used judiciously; `let` for null guards, `apply` for builders, `also` for logging; rare nesting beyond 2 levels
- **`runCatching`** at the **use case layer only** — `BaseUseCase.invoke(param): Result<R>` wraps `execute(param): R` exactly once
- **Never catch `CancellationException`** — `launchResult` + `BaseUseCase` both honor this via `runCatching` + structured `viewModelScope`

For the full set of strict-KMP rules: see `~/.claude/skills/kmp-rules/` (cross-cutting
conventions), `~/.claude/skills/kmp-code-pattern/` (MVVM contracts), `~/.claude/skills/kmp-review/`
(audit checks), `~/.claude/skills/kmp-add-form/` (form recipe), `~/.claude/skills/kmp-layout-pattern/`
(unified page layout).

When in doubt: **the project's `CLAUDE.md` supersedes the general patterns above** when working
in that codebase. Outside such projects, the general patterns apply as written.
