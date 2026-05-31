---
name: pharmacy-kmp-test
description: Write a ViewModel unit test in the pharmacy-app KMP companion using runVmTest + Fake<X>Repository. Enforces the coverage rule — every VM ships a <X>ViewModelTest.kt and non-trivial use cases get a unit test. Use when adding/updating tests in /Users/admin/ProjectPos/pharmacy-app/app-kmp.
---

# pharmacy-kmp-test

Tests live in `commonTest` (JVM-run via `jvmTest`). **No comments anywhere**, including in test
code and fakes. ~513 `@Test` functions across ~70 files today.

## Coverage rule
- **Every ViewModel** ships a `<X>ViewModelTest.kt` next to it under
  `features/<feat>/src/commonTest/.../presentation/<feat>/`.
- **Non-trivial use cases / parsers / pricing / validators** get a unit test in `:core:domain`.
- `:composeApp`'s `AppModuleWiringTest` already guards every `factoryOf` binding — don't duplicate it.

## The test harness — `runVmTest`

`core/ui/.../ui/common/RunVmTest.kt`:
```kotlin
fun runVmTest(block: suspend TestScope.(AppDispatchers) -> Unit) = runTest {
    val dispatcher = StandardTestDispatcher(testScheduler)
    val testDispatchers = AppDispatchers(main = dispatcher, io = dispatcher, default = dispatcher)
    Dispatchers.setMain(dispatcher)
    try { block(testDispatchers) } finally { Dispatchers.resetMain() }
}
```
It hands you an `AppDispatchers` wired to the test scheduler — pass it into use cases.

## Fakes — `:features:test-fixtures` (15 shared fakes)
Shared fakes live in `features/test-fixtures/.../domain/repository/Fake<X>Repository.kt` (in
**commonMain**, not commonTest, so any feature can depend on them). They expose seed/throw config
+ call-tracking, and **throw `RuntimeException` as a deliberate test signal** (the only A28-exempt
module). If a fake is used by exactly one feature, **co-locate** it in that feature's `commonTest`.

```kotlin
class FakeDrugRepository(
    private val seed: List<Drug> = emptyList(),
    private val listThrows: Boolean = false,
) : DrugRepository {
    var listCallCount = 0; private set
    override suspend fun list(): List<Drug> {
        listCallCount++
        if (listThrows) throw RuntimeException("list failed")
        return seed
    }
}
```

## Canonical test

```kotlin
class CustomersViewModelTest {
    private fun newVm(d: AppDispatchers, repo: FakeCustomerRepository) =
        CustomersViewModel(GetCustomersUseCase(repo, d))

    @Test
    fun load_populates_state() = runVmTest { d ->
        val vm = newVm(d, FakeCustomerRepository(seed = listOf(customer("a"))))
        advanceUntilIdle()
        assertEquals(1, vm.state.value.customers.size)
        assertFalse(vm.state.value.loading)
    }

    @Test
    fun load_failure_sets_error() = runVmTest { d ->
        val vm = newVm(d, FakeCustomerRepository(listThrows = true))
        advanceUntilIdle()
        assertNotNull(vm.state.value.error)
    }
}
```

Rules of thumb:
- Always `advanceUntilIdle()` after constructing a VM (its `init` launches work) and after each action.
- Assert directly on `vm.state.value`.
- Cover at least: initial/success path, failure path (`error` set, `loading` false), and any
  domain-specific branch (FEFO split, oversell, KY-required calc, tier price fallback, etc.).

## build.gradle.kts (only when using shared fakes)
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
