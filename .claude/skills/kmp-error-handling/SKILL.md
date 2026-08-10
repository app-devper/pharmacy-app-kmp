---
name: kmp-error-handling
description: The typed-error pattern of the pharmacy app end-to-end — AppException in :core:common, HttpClient response validator translates HTTP, BaseUseCase wraps once, the UiState carries a typed errorState, and localization happens at render. Use when adding an error case, mapping a status code, or auditing for swallowed exceptions.
---

# kmp-error-handling

There is **no `String` error anywhere** in this project. An error is a typed
`AppException` from the moment it is created until the instant it is rendered.
Generic exceptions in production fail the build (audit A28).

```
HTTP failure
   │ HttpResponseValidator (:core:data/network/HttpClient.kt) throws a typed AppException
   ▼
[ Repository ] ──→ throws AppException (bare T return, no runCatching)
   │ BaseUseCase.invoke wraps once → Result<R>
   ▼
[ UseCase ] ──→ Result<R>
   │ ViewModel.launchResult { onFailure = { setState { copy(errorState = <Feature>Error.X(it)) } } }
   ▼
[ UiState ] ──→ errorState: AppException?
   │ Content: state.errorState?.localize<X>(pharmStrings)
   ▼
[ ErrorBottomSheet(message: String?, onDismiss) ]
```

## 1. Transport exceptions — `:core:common/AppException.kt`

```kotlin
abstract class AppException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)
```

Nine concrete subclasses, each with a default message so call sites can throw
bare: `AuthException`, `ForbiddenException`, `NotFoundException`,
`ConflictException(payload: String?)`, `NetworkException`,
`ServerException(statusCode: Int?, body: String?)`, `ValidationException`,
`StorageException`, `UnsupportedPlatformException`.

Their `message` values are **English internal identifiers**, not user copy —
localization happens at render, so nothing here is ever shown as-is. The one
exception is `ValidationException`, whose message is domain-authored and passes
through verbatim.

## 2. Translation happens in the HttpClient, not in repositories

`:core:data/data/network/HttpClient.kt` installs an `HttpResponseValidator`:

```kotlin
HttpResponseValidator {
    validateResponse { response ->
        if (response.status.isSuccess()) return@validateResponse
        val body = response.bodyAsText()
        throw when (response.status) {
            HttpStatusCode.Unauthorized -> {
                tokenStorage.clear(); sessionExpiry.markExpired(); AuthException()
            }
            HttpStatusCode.Forbidden -> ForbiddenException()
            HttpStatusCode.NotFound  -> NotFoundException()
            HttpStatusCode.Conflict  -> ConflictException(payload = body)
            else -> ServerException(statusCode = response.status.value, body = body)
        }
    }
    handleResponseExceptionWithRequest { cause, _ ->
        if (cause is CancellationException) throw cause
        if (cause is AppException) throw cause
        throw NetworkException(cause = cause)
    }
}
```

So repositories and Apis contain **no try/catch at all** — every HTTP call
already surfaces a typed exception. A `try/catch` inside a `RepositoryImpl` is
a smell, not the pattern. Repositories only map DTO ↔ domain.

401 has a side effect on purpose: it clears the token and marks the session
expired, which is what drives the app back to the login graph.

## 3. Domain validation is structured, not stringly

`:core:domain/validation/`:

- `FieldValidationError(field: FieldLabel, …)` — `Required`, `InvalidDate`,
  `NotANumber`, `MustBePositive`, `MustBeNonNegative`; thrown by the `Field.*`
  validators. `:core:ui` composes rule × field label into copy.
- `SaleValidationError` — `EmptyCart`, `Return*`, `VoidReasonRequired`.
- `BulkImportParseError` — `EmptyInput`, `NotArrayOrObject`, `RowNotObject(row)`,
  `RowMissingName`.

## 4. Feature and common UiState errors

Generic operations reuse `:core:common/error/CommonUiStateError`:
`LoadFailed`, `SaveFailed`, `DeleteFailed`, `ExportFailed` — each takes a
`cause` so the original is preserved for logs.

Anything feature-specific gets a sealed class in
`presentation/<x>/exception/<X>UiStateError.kt`:

```kotlin
sealed class CustomersListUiStateError(message: String, cause: Throwable? = null) : AppException(message, cause) {
    class LoadCustomersFailed(cause: Throwable? = null) :
        CustomersListUiStateError("customers.list_load_failed", cause)
}
```

The message is a dotted key for logs; the user-facing text lives in
`PharmStrings`.

## 5. The ViewModel stores it typed

```kotlin
fun reload() {
    setState { copy(loading = true, errorState = null) }
    launchResult(
        block = { getCustomers() },
        onSuccess = { list -> setState { copy(loading = false, customers = list) } },
        onFailure = { e -> setState { copy(loading = false, errorState = CustomersListUiStateError.LoadCustomersFailed(e)) } },
    )
}
```

- The UiState exposes `errorState: AppException?` plus
  `override val domainError get() = errorState` and `withDomainError(...)` from
  `LoadableUiState` / `BaseFormUiState`.
- `BaseLoadableViewModel` provides `dismissError()`; `BaseFormViewModel` routes
  save failures through `mapSaveError(cause)` — passes `AppException`s through,
  wraps unknowns in `CommonUiStateError.SaveFailed`.
- **Never localize in a ViewModel.** No `pharmStrings`, no Thai literals.
- Info messages are a parallel channel, not errors: a plain sealed
  `messageState` (`CommonUiStateMessage.{Saved, ExportEmpty, ExportDone}` or a
  feature-specific one) rendered as a toast.

## 6. Localize at render

Each feature ships `presentation/<x>/i18n/<X>ErrorLocalize.kt`:

```kotlin
fun AppException.localizeCustomersList(s: PharmStrings): String = when (this) {
    is CustomersListUiStateError.LoadCustomersFailed -> s.customersListLoadFailed
    else -> localizeCommon(s)
}
```

`localizeCommon` (`:core:ui/i18n/CommonErrorLocalize.kt`) is the fallback: it
covers `FieldValidationError` (rule × field label), all four
`CommonUiStateError`s, all nine transport types, passes
`ValidationException.message` through verbatim, and lands on
`s.commonErrorGeneric`.

Render:

```kotlin
ErrorBottomSheet(
    message = state.errorState.unlessPageShowsError(rows.isEmpty())?.localizeCustomersList(s),
    onDismiss = callbacks.onDismissError,
)
```

`unlessPageShowsError` keeps the sheet quiet when the page is already showing
`PharmErrorState` for an empty failed list — one message, and it does not
vanish on dismiss.

No SnackBar for errors — the bottom sheet demands acknowledgement. Snackbars
(`LocalPharmSnackbar` / `PharmToast`) are for success and info only.

## 7. Adding a new error case

1. Add the case to the feature's `exception/<X>UiStateError.kt` (or reuse
   `CommonUiStateError`), taking `cause`.
2. Add the copy key to the feature's group interface in
   `:core:ui/i18n/groups/<Feature>Strings.kt` **and** its `Th` and `En` objects.
3. Map it in `presentation/<x>/i18n/<X>ErrorLocalize.kt` before the
   `else -> localizeCommon(s)`.
4. Set it from the ViewModel's `onFailure`.
5. Test it — assert the typed class, not a string.

## 8. Testing

```kotlin
@Test
fun load_failure_sets_typed_error_and_clears_loading() = runVmTest { d ->
    val repo = FakeCustomerRepository(listThrows = true)
    val vm = CustomersListViewModel(GetCustomersUseCase(repo, d))
    advanceUntilIdle()
    assertIs<CustomersListUiStateError.LoadCustomersFailed>(vm.state.value.errorState)
    assertFalse(vm.state.value.loading)
}
```

Assert on the type. Asserting on `message` couples the test to a log key, and
asserting on rendered copy belongs in a localization test, not a VM test.

`:features:test-fixtures` is the only A28-exempt module — its fakes throw
generic exceptions on purpose.

## 9. Anti-patterns

- `throw Exception/RuntimeException/IllegalStateException` in production → A28.
- `runCatching` inside a `RepositoryImpl` or a use case `execute()` — the
  transport already translates and `BaseUseCase` already wraps.
- `try/catch` in a repository to convert HTTP errors — that job belongs to the
  HttpClient validator.
- Catching `Throwable`, or catching `CancellationException` without rethrowing.
- A `String` error field on a UiState, or `e.message` shown to the user.
- Localizing in the ViewModel, or a Thai literal outside `PharmStringsTh` (A29).
- A ViewModel that re-throws instead of landing on a state.
