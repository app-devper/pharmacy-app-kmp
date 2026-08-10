---
name: kmp-test
description: Write a ViewModel unit test in the pharmacy app using runVmTest + a Fake<X>Repository from :features:test-fixtures. Covers the coverage rule, the kover floor, and what to assert. Use when adding or updating tests.
---

# kmp-test

Tests live in `commonTest` and run on the JVM via `jvmTest`. No comments
anywhere, including in tests and fakes — descriptive `@Test` names instead.

Current size: **1,253 `@Test` functions across 179 commonTest files**
(`grep -rn '@Test' core features composeApp --include='*.kt' | grep -v /build/ | wc -l`).

## Coverage rules

- **Every ViewModel** ships a `<X>ViewModelTest.kt` beside it in
  `features/<feat>/src/commonTest/kotlin/app/devper/pharm/presentation/<feat>/`.
- **Non-trivial use cases, parsers, validators and pricing logic** get a unit
  test in `:core:domain`. Pure helpers extracted from a primitive (a fit
  calculation, a predicate) get one in `:core:ui`.
- `composeApp/src/commonTest/…/di/AppModuleWiringTest.kt` instantiates every VM
  through the real DI graph — it already guards every `factoryOf`, so don't
  duplicate that per feature.
- **`koverVerify` enforces a line-coverage floor** (`COVERAGE_FLOOR` in the root
  `build.gradle.kts`, currently **55**). It is a ratchet toward 80, not a
  one-shot gate: when you add tests that push coverage up, raise the floor in
  the same PR. `./gradlew koverHtmlReport` → `build/reports/kover/html/`.
  Kover excludes UI composables, the i18n string tables, `ui.print` and DTOs —
  what is measured is domain / use case / VM / mapper / localizer.

## The harness — `runVmTest`

```kotlin
fun runVmTest(block: suspend TestScope.(AppDispatchers) -> Unit) = runTest {
    val dispatcher = StandardTestDispatcher(testScheduler)
    val testDispatchers = AppDispatchers(main = dispatcher, io = dispatcher, default = dispatcher)
    Dispatchers.setMain(dispatcher)
    try { block(testDispatchers) } finally { Dispatchers.resetMain() }
}
```

Lives in `:core:ui/ui/common/RunVmTest.kt`. It hands you an `AppDispatchers`
wired to the test scheduler — pass it to every use case constructor.
`advanceUntilIdle()` advances virtual time.

## Fakes — `:features:test-fixtures`

18 shared fakes in
`features/test-fixtures/src/commonMain/kotlin/app/devper/pharm/domain/repository/`
— **commonMain, not commonTest**, so any feature can depend on them with
`implementation(project(":features:test-fixtures"))`.

Convention: constructor flags for seeding and failure, plus captured
call-tracking properties with private setters.

```kotlin
class FakeCustomerRepository(
    private val seed: List<Customer> = emptyList(),
    private val listThrows: Boolean = false,
    private val addThrowsOn: String? = null,
) : CustomerRepository {

    var lastAdd: CustomerInput? = null
        private set

    override suspend fun list(): List<Customer> {
        if (listThrows) throw ServerException("list failed")
        return seed
    }

    override suspend fun add(input: CustomerInput): Customer {
        if (input.name == addThrowsOn) throw ServerException("backend rejected: $addThrowsOn")
        lastAdd = input
        return …
    }
}
```

This is the only A28-exempt module — fakes may throw whatever makes the test
signal clearest. Prefer typed exceptions anyway, since VM tests assert on the
resulting `errorState` type.

If a fake is used by exactly one feature, co-locate it in that feature's
`commonTest` instead of growing the shared module.

## Canonical list-VM test

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class CustomersListViewModelTest {

    private fun customer(id: String) = Customer(
        id = id, name = "Customer $id", phone = null, priceTier = "", allergyNote = null,
    )

    @Test
    fun init_loads_customers() = runVmTest { d ->
        val repo = FakeCustomerRepository(seed = listOf(customer("a"), customer("b")))
        val vm = CustomersListViewModel(GetCustomersUseCase(repo, d))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.customers.size)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.errorState)
    }

    @Test
    fun load_failure_uses_customer_specific_error() = runVmTest { d ->
        val vm = CustomersListViewModel(GetCustomersUseCase(FakeCustomerRepository(listThrows = true), d))
        advanceUntilIdle()
        assertIs<CustomersListUiStateError.LoadCustomersFailed>(vm.state.value.errorState)
        assertFalse(vm.state.value.loading)
    }
}
```

**Assert the error type, not its message.** The message is a dotted log key;
the user-facing copy lives in `PharmStrings` and is tested separately.

## Canonical form-VM test

A `newVm(...)` helper returning the VM keeps the multi-use-case constructors
readable:

```kotlin
private fun newVm(
    dispatchers: AppDispatchers,
    repo: FakeCustomerRepository = FakeCustomerRepository(),
): Pair<CustomerFormViewModel, FakeCustomerRepository> {
    val vm = CustomerFormViewModel(
        getCustomers = GetCustomersUseCase(repo, dispatchers),
        addCustomer = AddCustomerUseCase(repo, dispatchers),
        updateCustomer = UpdateCustomerUseCase(repo, dispatchers),
    )
    return vm to repo
}
```

Cover, at minimum:

| Case | Assert |
|---|---|
| add mode starts empty | fields blank, `!canSubmit`, `!hasUnsavedChanges` |
| edit mode hydrates | fields populated **and** `!hasUnsavedChanges` (baseline was set) |
| required field fills | `canSubmit` flips true |
| `submit()` succeeds | `saved`, `!saving`, and the repo captured the right input |
| `submit()` fails | typed `errorState`, `!saving` |
| `submit()` when `!canSubmit` | no-op — nothing captured, `!saved` |

## Rules of thumb

- **Always `advanceUntilIdle()`** after constructing a VM (its `init` launches
  work) and after each action.
- Assert directly on `vm.state.value`. Reach for Turbine only when you need to
  verify intermediate emissions.
- Cover the success path, the failure path, and every domain-specific branch
  (validation, conditional reload, special-case computation).
- Name the test after the behaviour — that name is the documentation.

## Per-feature build.gradle.kts

```kotlin
commonTest.dependencies {
    implementation(libs.kotlinx.coroutines.test)
    implementation(project(":features:test-fixtures"))
}
```

## Verify

```bash
./gradlew :features:<feat>:jvmTest          # one feature
./gradlew koverVerify                        # coverage floor
./gradlew :composeApp:check                  # audit + full dependent tree
```
