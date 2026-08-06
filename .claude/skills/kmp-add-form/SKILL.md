---
name: kmp-add-form
description: Add a create/edit form screen in a Compose Multiplatform project using the BaseFormViewModel pattern (saving/saved/error transitions, canSubmit gating, FormField + BrandTextField layout, save-in-toolbar). Use when building an add/edit screen with validation.
---

# kmp-add-form

Forms extend `BaseFormViewModel<S>` (in `:core:ui`) instead of plain `BaseViewModel`. It
standardises submit gating + `saving/saved/error` transitions so every form behaves identically.

**No comments. `Brand*` primitives only (no raw Material 3 in net-new).** Replace `Brand*` with
your project's design-system prefix.

## 1. The base contracts (place in `:core:ui/ui/common/`)

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
    fun submit() {
        if (!current.canSubmit) return
        setState { withSaving(true).withError(null) }
        viewModelScope.launch {
            persist()
                .onSuccess { setState { withSaving(false).withSaved(true) } }
                .onFailure { e -> setState { withSaving(false).withError(e.message ?: saveErrorFallback) } }
        }
    }
    fun dismissError() = setState { withError(null) }
    fun resetSaved() = setState { withSaved(false) }
}
```

`submit()` is fully provided: no-ops when `!canSubmit`, sets `saving=true`, runs `persist()`,
lands on `saved=true` or `error=...`. You implement `persist()` and the field setters.

## 2. UiState — implement the F-bounded interface

```kotlin
data class CustomerFormUiState(
    val name: String = "",
    val phone: String = "",
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    override val error: String? = null,
) : BaseFormUiState<CustomerFormUiState> {
    val titleLabel: String get() = if (mode is Edit) "แก้ไขลูกค้า" else "เพิ่มลูกค้า"
    override val canSubmit: Boolean get() = name.isNotBlank() && !saving
    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override fun withError(error: String?) = copy(error = error)
}
```

`canSubmit` is the **single source of validation truth** — gate the submit button on it. Derive
it from field values + `!saving`. Computed properties (`titleLabel`, `canSubmit`) live in the
UiState, not the VM.

## 3. ViewModel — implement persist() + field setters

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
Inject `getCustomer: GetCustomerUseCase` + `updateCustomer: UpdateCustomerUseCase`; choose the
right one inside `persist()` based on the `mode` field.

## 4. Callbacks + Screen + Content (file-per-class)

### Callbacks
```kotlin
data class CustomerFormCallbacks(
    val onBack: () -> Unit = {},
    val onNameChange: (String) -> Unit = {},
    val onPhoneChange: (String) -> Unit = {},
    val onSubmit: () -> Unit = {},
    val onDismissError: () -> Unit = {},
)
```

### Screen
```kotlin
@Composable
fun CustomerFormScreen(
    onBack: () -> Unit,
    vm: CustomerFormViewModel = koinViewModel(),
) {
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(state.saved) {
        if (state.saved) { vm.resetSaved(); onBack() }
    }
    CustomerFormContent(
        state = state,
        callbacks = CustomerFormCallbacks(
            onBack = onBack,
            onNameChange = vm::onNameChange,
            onPhoneChange = vm::onPhoneChange,
            onSubmit = vm::submit,
            onDismissError = vm::dismissError,
        ),
    )
}
```

### Content
Use the unified sub-page layout (from **kmp-layout-pattern**): a `Column { BrandListToolbar
(onBack, actions = BrandSaveAction) ; verticalScroll-padded column of BrandFormCard sections }`.
**No bottom save bar. No inline Cancel button** (back arrow is the way out).

```kotlin
@Composable
fun CustomerFormContent(
    state: CustomerFormUiState,
    callbacks: CustomerFormCallbacks = CustomerFormCallbacks(),
) {
    val t = brandTokens
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        BrandListToolbar(
            title = state.titleLabel,
            onBack = callbacks.onBack,
            actions = {
                BrandSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSubmit,
                    onSubmit = callbacks.onSubmit,
                )
            },
        )
        Column(
            modifier = Modifier
                .weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.loading) {
                Box(Modifier.fillMaxWidth().padding(vertical = 48.dp), contentAlignment = Alignment.Center) {
                    BrandCircularProgress(color = t.colors.accent)
                }
            } else {
                BrandFormCard(title = "ข้อมูลลูกค้า") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FormField(label = "ชื่อ", required = true) {
                            BrandTextField(value = state.name, onValueChange = callbacks.onNameChange)
                        }
                        FormField(label = "เบอร์โทร") {
                            BrandTextField(value = state.phone, onValueChange = callbacks.onPhoneChange)
                        }
                    }
                }
            }
        }
    }
    ErrorBottomSheet(state.error, callbacks.onDismissError)
}

@Preview @Composable
private fun CustomerFormContent_Empty_Preview() {
    BrandTheme { CustomerFormContent(state = CustomerFormUiState()) }
}
@Preview @Composable
private fun CustomerFormContent_Filled_Preview() {
    BrandTheme { CustomerFormContent(state = CustomerFormUiState(name = "สมศรี", phone = "0812345678")) }
}
@Preview @Composable
private fun CustomerFormContent_Saving_Preview() {
    BrandTheme { CustomerFormContent(state = CustomerFormUiState(name = "สมศรี", saving = true)) }
}
```

### FormField pattern (input layout stability)
- Wrap each input in `FormField(label = "...", required = true) { BrandTextField(...) }` —
  static label above, **not** the floating `label = {}` slot (M3's floating label causes layout
  jitter as the user types).
- Pin single-line inputs to `Modifier.height(56.dp)` to avoid per-keystroke bounce.
- Conditional `trailingIcon` must keep its slot reserved: always render the `IconButton`, gate
  the inner `Icon`, set `enabled = cond`.

## 5. DI + wire + test

- `di/<Feat>Module.kt`: `factoryOf(::CustomerFormViewModel)` (VMs only).
- Test via **kmp-test**:
  - assert `canSubmit` toggles when required fields fill/blank
  - assert `submit()` flips `saving → saved` on success
  - assert `submit()` flips `saving → error` on failure (use a `FakeCustomerRepository` configured to throw)
  - assert `submit()` no-ops when `canSubmit == false`

## Verify

```bash
./gradlew :features:<feat>:jvmTest :composeApp:auditArchitecture
```
