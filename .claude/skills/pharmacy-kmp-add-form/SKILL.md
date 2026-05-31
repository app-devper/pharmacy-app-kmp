---
name: pharmacy-kmp-add-form
description: Add a create/edit form screen in the pharmacy-app KMP companion using the BaseFormViewModel pattern (saving/saved/error transitions, canSubmit gating, FormField + PharmTextField layout). Use when building an add/edit screen with validation in /Users/admin/ProjectPos/pharmacy-app/app-kmp.
---

# pharmacy-kmp-add-form

Forms extend `BaseFormViewModel<S>` (in `:core:ui`, `app.devper.pharm.ui.common`) instead of
plain `BaseViewModel`. It standardises submit gating + saving/saved/error transitions.

**No comments. Thai user-facing copy. `Pharm*` primitives only (no M3 in net-new).**

## The base contracts

```kotlin
interface BaseFormUiState<S : BaseFormUiState<S>> : BaseUiState {
    val saving: Boolean
    val saved: Boolean
    val canSubmit: Boolean
    fun withSaving(saving: Boolean): S
    fun withSaved(saved: Boolean): S
    fun withError(error: String?): S
}

abstract class BaseFormViewModel<S : BaseFormUiState<S>>(initial: S) : BaseViewModel<S>(initial) {
    protected open val saveErrorFallback: String = "บันทึกไม่สำเร็จ"
    protected abstract suspend fun persist(): Result<Unit>
    fun submit() { /* gates on canSubmit, flips saving→saved/error */ }
    fun dismissError() = setState { withError(null) }
    fun resetSaved() = setState { withSaved(false) }
}
```

`submit()` is fully provided: it no-ops when `!canSubmit`, sets `saving=true`, runs `persist()`,
and lands on `saved=true` or `error=...`. You implement `persist()` and the field setters.

## 1. UiState — implement the F-bounded interface

```kotlin
data class CustomerFormUiState(
    val name: String = "",
    val phone: String = "",
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val error: String? = null,
) : BaseFormUiState<CustomerFormUiState> {
    override val canSubmit: Boolean get() = name.isNotBlank() && !saving
    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}
```
`canSubmit` is the **single source of validation truth** — gate the submit button on it.

## 2. ViewModel — implement persist() + field setters

```kotlin
class CustomerFormViewModel(
    private val addCustomer: AddCustomerUseCase,
) : BaseFormViewModel<CustomerFormUiState>(CustomerFormUiState()) {
    fun onNameChange(v: String) = setState { copy(name = v) }
    fun onPhoneChange(v: String) = setState { copy(phone = v) }
    override suspend fun persist(): Result<Unit> =
        addCustomer(AddCustomerParam(name = current.name, phone = current.phone)).map { }
}
```
For **edit** forms, load existing values in `init { launchResult(...) { setState { copy(...) } } }`.

## 3. Callbacks + Screen + Content
- Callbacks: `onNameChange: (String) -> Unit`, `onPhoneChange`, `onSubmit`, `onDismissError`.
- Screen collects `collectAsStateWithLifecycle()` and watches `saved` to navigate back:
  ```kotlin
  LaunchedEffect(state.saved) { if (state.saved) { vm.resetSaved(); onDone() } }
  ```
- Content uses the **FormField pattern** for layout stability:
  - Wrap each input in `FormField(label = "ชื่อ", required = true) { PharmTextField(...) }` — static label above, **not** the floating `label = {}` slot.
  - Pin single-line fields to `Modifier.height(56.dp)` to avoid per-keystroke bounce.
  - Conditional `trailingIcon` must keep its slot reserved: always render the `IconButton`, gate the inner `Icon`, set `enabled = cond`.
  - Submit button: `PharmButton(label = "บันทึก", enabled = state.canSubmit, loading = state.saving, onClick = callbacks.onSubmit)`.
  - End with `ErrorBottomSheet(state.error, callbacks.onDismissError)`.

## 4. DI + wire + test
`di/<Feat>Module.kt`: `factoryOf(::CustomerFormViewModel)` (VMs only). Test via `pharmacy-kmp-test`:
assert `canSubmit` toggling, `saving→saved` on success, and `error` set on failure (use a
`Fake<X>Repository` configured to throw).

## Verify
```bash
./gradlew :features:<feat>:jvmTest :composeApp:auditArchitecture
```
