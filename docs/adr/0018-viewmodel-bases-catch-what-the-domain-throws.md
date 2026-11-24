# ViewModel bases catch what the domain throws

Repositories in this app throw a typed `AppException` and return the bare value; a use case extends `BaseUseCase` and returns `Result`. The ViewModel bases used to take `Result`-returning lambdas, so a ViewModel that called a repository wrapped the call by hand: `launchResult(block = { Result.success(customers.list()) })`, `fetch = { Result.success(...) }`, and `persist()` ending in `return Result.success(Unit)`. 37 ViewModels carried 69 such lines, and the wrap added nothing: the base already caught what the block threw.

The bases are now the one place a failure becomes state, and they take the block as it is.

- `launchCatching(block, onSuccess, onFailure)` runs a block that throws.
- `BaseLoadableViewModel.load(fetch, failure, onLoaded)` takes a throwing `fetch`.
- `BaseFormViewModel.persist()` returns `Unit` and throws to fail; `mapSaveError` receives the exception unwrapped.
- `launchResult(block, onSuccess, onFailure)` stays for a use case that returns `Result` (sign-in, void, return, print, the offline queue).
- `catching { }` stays for reads run in parallel that must not cancel each other.

All of them rethrow `CancellationException`. A `Result` met inside a throwing block is unwrapped with `.getOrThrow()`; left as a bare expression it would be ignored and the failure reported as success.

A ViewModel calls a use case when there is a rule to apply or more than one repository to coordinate. A plain read or write calls the repository interface in `:core:domain`. A use case that only forwards to one repository method is not written.

Audit rule A31 fails the build on `Result.success(`, `Result.failure(` or a `block` / `fetch` lambda that starts with `runCatching` in a `*ViewModel.kt` under `:features` or `:composeApp` commonMain. `runCatching` in a suspend block also swallows cancellation, which the bases do not.

We did not move the boundary into the domain (every repository returning `Result`, or a use case in front of every repository call). It would change every repository interface and every fake for the same behaviour, and the typed exceptions already carry what the UI needs.
