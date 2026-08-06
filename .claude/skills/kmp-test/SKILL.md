---
name: kmp-test
description: Write a ViewModel unit test in a Compose Multiplatform project using runVmTest + Fake<X>Repository. Enforces the coverage rule — every VM ships a <X>ViewModelTest.kt and non-trivial use cases get a unit test. Use when adding/updating tests.
---

# kmp-test

Tests live in `commonTest` (JVM-run via `jvmTest`). **No comments anywhere**, including in test
code and fakes — descriptive function names instead.

## Coverage rule

- **Every ViewModel** ships a `<X>ViewModelTest.kt` next to it under
  `features/<feat>/src/commonTest/.../presentation/<feat>/`. **Audit it**: grep every
  `*ViewModel.kt` against a matching `*ViewModelTest.kt`. The only acceptable exception is a VM
  with no injectable dependencies (e.g. it reads a bundled resource) and the parsing logic is
  covered separately.
- **Non-trivial use cases / parsers / validators / pricing** get a unit test in `:core:domain`.
- `:composeApp`'s `AppModuleWiringTest` (instantiates every VM via the DI graph) already guards
  every `factoryOf` binding — don't duplicate it per feature.

## The test harness — `runVmTest` (place in `:core:ui/ui/common/`)

```kotlin
fun runVmTest(block: suspend TestScope.(AppDispatchers) -> Unit) = runTest {
    val dispatcher = StandardTestDispatcher(testScheduler)
    val testDispatchers = AppDispatchers(main = dispatcher, io = dispatcher, default = dispatcher)
    Dispatchers.setMain(dispatcher)
    try { block(testDispatchers) } finally { Dispatchers.resetMain() }
}
```

It hands you an `AppDispatchers` wired to the test scheduler — pass it into use cases that take
one in their constructor. `advanceUntilIdle()` advances virtual time.

## Fakes — `:features:test-fixtures`

Shared fakes live in `features/test-fixtures/.../domain/repository/Fake<X>Repository.kt` in
**commonMain** (not commonTest), so any feature module can depend on them via
`implementation(project(":features:test-fixtures"))`. They expose `seed`/`throws` config + call
tracking, and may **throw `RuntimeException` as a deliberate test signal** (the only A28-exempt
module).

```kotlin
class FakeCustomerRepository(
    private val seed: List<Customer> = emptyList(),
    private val listThrows: Boolean = false,
) : CustomerRepository {
    var listCallCount = 0; private set
    val captured = mutableListOf<AddCustomerParam>()

    override suspend fun list(): List<Customer> {
        listCallCount++
        if (listThrows) throw RuntimeException("list failed")
        return seed
    }
    override suspend fun add(param: AddCustomerParam): Customer {
        captured += param
        return Customer(id = "new", name = param.name, phone = param.phone)
    }
}
```

If a fake is used by exactly one feature, **co-locate** it in that feature's `commonTest` rather
than growing the shared module.

## Canonical test (list VM)

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class CustomersListViewModelTest {
    private fun customer(id: String) = Customer(id = id, name = "Customer $id", phone = null)

    @Test
    fun load_populates_state() = runVmTest { d ->
        val repo = FakeCustomerRepository(seed = listOf(customer("a"), customer("b")))
        val vm = CustomersListViewModel(GetCustomersUseCase(repo, d))
        advanceUntilIdle()
        assertEquals(2, vm.state.value.customers.size)
        assertFalse(vm.state.value.loading)
        assertNull(vm.state.value.error)
    }

    @Test
    fun load_failure_sets_error_and_clears_loading() = runVmTest { d ->
        val vm = CustomersListViewModel(GetCustomersUseCase(FakeCustomerRepository(listThrows = true), d))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun query_change_updates_state() = runVmTest { d ->
        val vm = CustomersListViewModel(GetCustomersUseCase(FakeCustomerRepository(), d))
        advanceUntilIdle()
        vm.onQueryChange("สมศรี")
        assertEquals("สมศรี", vm.state.value.query)
    }
}
```

## Canonical test (form VM — `BaseFormViewModel`)

```kotlin
@Test
fun submit_valid_saves_and_records_form() = runVmTest { d ->
    val repo = FakeCustomerRepository()
    val vm = CustomerFormViewModel(AddCustomerUseCase(repo, d))
    vm.onNameChange("สมศรี")
    vm.onPhoneChange("0812345678")
    vm.submit()
    advanceUntilIdle()
    assertTrue(vm.state.value.saved)
    assertEquals("สมศรี", repo.captured.first().name)
}

@Test
fun submit_blank_is_noop() = runVmTest { d ->
    val repo = FakeCustomerRepository()
    val vm = CustomerFormViewModel(AddCustomerUseCase(repo, d))
    vm.submit()
    advanceUntilIdle()
    assertFalse(vm.state.value.saved)
    assertTrue(repo.captured.isEmpty())
}

@Test
fun submit_failure_sets_error_and_clears_saving() = runVmTest { d ->
    val repo = FakeCustomerRepository(addThrows = true)
    val vm = CustomerFormViewModel(AddCustomerUseCase(repo, d))
    vm.onNameChange("สมศรี")
    vm.submit()
    advanceUntilIdle()
    assertNotNull(vm.state.value.error)
    assertFalse(vm.state.value.saving)
}
```

## Rules of thumb

- **Always `advanceUntilIdle()`** after constructing a VM (its `init` launches work) and after
  each action.
- **Assert directly on `vm.state.value`** — no Turbine / Flow.test() for the basic shape (use
  Turbine when you need to verify intermediate emissions).
- **Cover at least**: initial/success path, failure path (`error` set, `loading` false), and any
  domain-specific branch (validation, special-case computations, conditional reloads).
- **No comments in tests** — name `@Test` functions descriptively.

## build.gradle.kts (per feature)

Only needed when the feature uses shared fakes:

```kotlin
commonTest.dependencies {
    implementation(libs.kotlinx.coroutines.test)
    implementation(project(":features:test-fixtures"))
}
```

## Verify

```bash
./gradlew :features:<feat>:jvmTest          # one feature
./gradlew :composeApp:check                 # full dependent tree
```
