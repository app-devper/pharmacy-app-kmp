---
name: kmp-error-handling
description: The typed-error pattern for a Compose Multiplatform Clean-Architecture project — AppException hierarchy in :core:common, RepositoryImpl translates Ktor/network/IO errors to typed exceptions, BaseUseCase wraps once into Result<T>, ViewModel renders state.error, Screen shows ErrorBottomSheet. Use when adding a new error case, mapping HTTP status, or auditing for swallowed exceptions.
---

# kmp-error-handling

The project bans generic exceptions in production (audit A28). Errors flow through a fixed
3-layer translation:

```
HTTP / IO failure (Ktor throws ClientRequestException, IOException, …)
   │
   │ RepositoryImpl catches + throws typed AppException subclass
   ▼
[ Repository ] ──→ throws AppException
   │
   │ BaseUseCase wraps once via runCatching → Result<R>
   ▼
[ UseCase ] ──→ returns Result<R>
   │
   │ ViewModel.launchResult(...) onSuccess { copy(...) } onFailure { copy(error = e.message) }
   ▼
[ ViewModel ] ──→ state.error: String?
   │
   ▼
[ Screen ] renders ErrorBottomSheet(state.error, onDismiss = vm::dismissError)
```

Domain code, use cases, and ViewModels **NEVER** throw generic `Exception`/`RuntimeException`/
`IllegalStateException`. Fakes in `:features:test-fixtures` are the only A28-exempt module.

## 1. The `AppException` hierarchy in `:core:common`

```kotlin
// core/common/src/commonMain/kotlin/<base>/common/AppException.kt
sealed class AppException(message: String, cause: Throwable? = null) : Throwable(message, cause)

class AuthException(message: String, cause: Throwable? = null) : AppException(message, cause)
class ForbiddenException(message: String, cause: Throwable? = null) : AppException(message, cause)
class NotFoundException(message: String, cause: Throwable? = null) : AppException(message, cause)
class ConflictException(message: String, cause: Throwable? = null) : AppException(message, cause)
class ValidationException(message: String, cause: Throwable? = null) : AppException(message, cause)
class NetworkException(message: String, cause: Throwable? = null) : AppException(message, cause)
class ServerException(message: String, cause: Throwable? = null) : AppException(message, cause)
class StorageException(message: String, cause: Throwable? = null) : AppException(message, cause)
class UnsupportedPlatformException(message: String, cause: Throwable? = null) : AppException(message, cause)
```

The naming maps roughly to HTTP semantics:
- **Auth (401)** — user not signed in / token expired
- **Forbidden (403)** — signed in but lacks permission
- **NotFound (404)** — record absent
- **Conflict (409)** — duplicate, optimistic-lock failure, invariant violated
- **Validation (4xx with body)** — client-side or server-side rule failed
- **Network** — no connection / DNS / TLS handshake / timeout
- **Server (5xx)** — backend error
- **Storage** — local disk / preferences / IndexedDB / MediaStore failure
- **UnsupportedPlatform** — the platform genuinely can't do this op (e.g. no native printer)

## 2. Translating Ktor errors in `RepositoryImpl`

The shared `HttpClient` has `expectSuccess = true`, so Ktor throws `ClientRequestException`
(4xx), `ServerResponseException` (5xx), or `IOException` (transport). Catch them at the
**repository boundary**, NOT inside `<X>Api`:

```kotlin
class CustomerRepositoryImpl(private val api: CustomerApi) : CustomerRepository {

    override suspend fun list(): List<Customer> = withTranslated { api.list().map { it.toDomain() } }
    override suspend fun add(p: AddCustomerParam): Customer = withTranslated { api.add(p.toRequest()).toDomain() }
    // … etc
}

private suspend inline fun <T> withTranslated(block: () -> T): T =
    try {
        block()
    } catch (e: ClientRequestException) {
        throw translateClient(e)
    } catch (e: ServerResponseException) {
        throw ServerException(e.message, e)
    } catch (e: IOException) {
        throw NetworkException(e.message ?: "เชื่อมต่อเครือข่ายไม่ได้", e)
    }
    // do NOT catch CancellationException; do NOT catch Throwable — let unknown errors fail loud

private fun translateClient(e: ClientRequestException): AppException = when (e.response.status.value) {
    401 -> AuthException("กรุณาเข้าสู่ระบบใหม่", e)
    403 -> ForbiddenException("คุณไม่มีสิทธิ์ดำเนินการนี้", e)
    404 -> NotFoundException("ไม่พบข้อมูล", e)
    409 -> ConflictException(e.message, e)
    422 -> ValidationException(parseValidationBody(e) ?: "ข้อมูลไม่ถูกต้อง", e)
    else -> ServerException(e.message, e)
}
```

Rules:
- Translate **only at the repository boundary** — never inside Apis (they stay plain Ktor),
  never inside use cases (they wrap once via `BaseUseCase`).
- **Never catch `CancellationException`** — always rethrow it (it carries coroutine cancellation
  semantics).
- **Never catch `Throwable`** — leave unknown failures loud so they're caught by review/CI
  instead of silently translated to a `ServerException`.
- The translator inline function is `private suspend inline fun <T>` to avoid the lambda
  allocation and to keep `suspend` happy.

## 3. `BaseUseCase` wraps once

```kotlin
abstract class BaseUseCase<P, R>(private val dispatchers: AppDispatchers) {
    abstract suspend fun execute(param: P): R
    suspend operator fun invoke(param: P): Result<R> =
        withContext(dispatchers.io) { runCatching { execute(param) } }
}
```

The use case **never** wraps in another `runCatching` (audit-flagged). It **never** catches
specific exceptions to recover — that's the VM's job via `onFailure`.

Known exceptions to "no runCatching in use cases" are checkout / multi-step submission flows
that explicitly want to recover mid-flight (e.g. retry the next bill on failure). Those should
be commented in the use case header **via the test name** (`@Test fun checkout_partial_failure_continues_with_remaining_bills()`), not via code comments.

## 4. ViewModel surfaces `state.error`

```kotlin
fun reload() {
    setState { copy(loading = true, error = null) }
    launchResult(
        block = { getCustomers() },
        onSuccess = { list -> setState { copy(loading = false, customers = list) } },
        onFailure = { e -> setState { copy(loading = false, error = e.message ?: "โหลดข้อมูลไม่สำเร็จ") } },
    )
}
fun dismissError() = setState { copy(error = null) }
```

Rules:
- The string in `state.error` is **already user-facing** (Thai for a Thai-first project). The
  `AppException` subclass carries one, repository translation builds one, fallback covers the
  rest.
- Special-case branching by type when the screen wants to react differently:
  ```kotlin
  onFailure = { e ->
      when (e) {
          is AuthException -> callbacks.onAuthExpired()
          is ConflictException -> setState { copy(conflict = e.message) }
          else -> setState { copy(error = e.message ?: "…") }
      }
  }
  ```
- VMs **never** re-throw. They land on a state.

## 5. Screen renders `ErrorBottomSheet`

Every screen ends with:
```kotlin
ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
```

`ErrorBottomSheet` is a `Brand*` primitive (see **kmp-design-system**): a bottom-sheet that
shows when `message != null`, closes on swipe-down or button tap, and calls `onDismiss`. **No
SnackBar** for errors — errors must demand acknowledgement.

## 6. Testing

- Repository tests with `MockEngine`:
  ```kotlin
  @Test fun list_401_translates_to_AuthException() = runTest {
      val engine = MockEngine { respond("", status = HttpStatusCode.Unauthorized) }
      val impl = CustomerRepositoryImpl(CustomerApi(httpClient(engine)))
      val ex = assertFailsWith<AuthException> { impl.list() }
      assertTrue(ex.message.contains("กรุณาเข้าสู่ระบบใหม่"))
  }
  ```
- VM tests with a `Fake<X>Repository` configured to throw a typed exception:
  ```kotlin
  @Test fun load_failure_sets_error_and_clears_loading() = runVmTest { d ->
      val vm = CustomersListViewModel(GetCustomersUseCase(FakeCustomerRepository(throws = NetworkException("offline")), d))
      advanceUntilIdle()
      assertEquals("offline", vm.state.value.error)
      assertFalse(vm.state.value.loading)
  }
  ```

Fakes are the only place that may `throw RuntimeException(...)` (deliberate A28-exempt test
signal); production code must use the typed subclasses.

## 7. Anti-patterns to flag

- **`throw Exception(...)` / `throw RuntimeException(...)` / `throw IllegalStateException(...)`
  in production code** → audit A28, fail.
- **`runCatching` in a `RepositoryImpl`** → swallows typed translation; let the typed exception
  propagate.
- **`runCatching` in a use case `execute()`** → `BaseUseCase.invoke` already wraps; double
  wrapping returns `Result<Result<R>>` semantically.
- **Catching `Throwable` / `CancellationException`** → masks bugs / breaks coroutine cancellation.
- **Repository impl translating to a `String` and rethrowing as a different shape** → keep the
  typed exception; the VM/use case differentiates by `is` checks.
- **VM rendering `e.toString()`** or `e::class.simpleName` to the user → use `e.message` with a
  fallback string.
- **SnackBar for an error** → use `ErrorBottomSheet` for anything that requires acknowledgement.
- **VM that re-throws** instead of setting `state.error` → never propagate from a VM.
