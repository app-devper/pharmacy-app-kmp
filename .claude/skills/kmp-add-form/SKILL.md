---
name: kmp-add-form
description: Add a create/edit form screen to the pharmacy app on BaseFormViewModel — canSubmit gating, saving/saved/typed-error transitions, unsaved-change guarding, FormField + PharmTextField layout, save-in-toolbar. Use when building an add/edit screen with validation.
---

# kmp-add-form

Forms extend `BaseFormViewModel<S>` (`:core:ui/ui/common/`) rather than plain
`BaseViewModel`, so submit gating and the `saving → saved | error` transitions
are identical everywhere. No comments; `Pharm*` primitives only.

Reference implementation: `:features:customers` —
`CustomerFormUiState.kt` / `CustomerFormViewModel.kt` / `CustomerFormScreen.kt`
+ `form/CustomerFormContent.kt` + `form/CustomerFormInfoSection.kt`.

## 1. The base contracts (already in `:core:ui`, don't reimplement)

```kotlin
interface BaseUiState {
    val loading: Boolean
    val domainError: AppException? get() = null
}

interface BaseFormUiState<S : BaseFormUiState<S>> : BaseUiState {
    val saving: Boolean
    val saved: Boolean
    val canSubmit: Boolean
    val hasUnsavedChanges: Boolean get() = false
    fun withSaving(saving: Boolean): S
    fun withSaved(saved: Boolean): S
    fun withDomainError(error: AppException?): S
}

abstract class BaseFormViewModel<S : BaseFormUiState<S>>(initial: S) : BaseViewModel<S>(initial) {
    protected open fun mapSaveError(cause: Throwable): AppException =
        cause as? AppException ?: CommonUiStateError.SaveFailed(cause)
    protected abstract suspend fun persist(): Result<Unit>
    fun submit() { … }        // no-ops when !canSubmit; saving → saved | domainError
    fun dismissError() = setState { withDomainError(null) }
    fun resetSaved() = setState { withSaved(false) }
}
```

Errors are **typed** — there is no `withError(String?)` anywhere. Override
`mapSaveError` when a save failure needs a feature-specific case; otherwise the
default wraps unknowns in `CommonUiStateError.SaveFailed`.

## 2. UiState — fields in a nested data class

Keep the editable fields in their own `data class` and hold a `baselineForm`
copy. That gives `hasUnsavedChanges` for free and keeps `copy()` calls small.

```kotlin
sealed interface CustomerFormMode {
    data object Add : CustomerFormMode
    data class Edit(val customerId: String) : CustomerFormMode
}

data class CustomerFormFields(
    val name: String = "",
    val phone: String = "",
    val priceTier: String = "",
)

data class CustomerFormUiState(
    val mode: CustomerFormMode = CustomerFormMode.Add,
    val form: CustomerFormFields = CustomerFormFields(),
    val baselineForm: CustomerFormFields = CustomerFormFields(),
    override val loading: Boolean = false,
    override val saving: Boolean = false,
    override val saved: Boolean = false,
    val errorState: AppException? = null,
) : BaseFormUiState<CustomerFormUiState> {
    override val canSubmit: Boolean
        get() = !saving && !loading && form.name.isNotBlank()
    override val hasUnsavedChanges: Boolean get() = form != baselineForm
    val isEdit: Boolean get() = mode is CustomerFormMode.Edit

    override fun withSaving(saving: Boolean) = copy(saving = saving)
    override fun withSaved(saved: Boolean) = copy(saved = saved)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
```

`canSubmit` is the single source of validation truth — gate the save button on
it. Computed properties live on the UiState, never in the VM.

**Money / Quantity stay `String` while editing.** Wrap at submit time
(`Money(form.price.toDoubleOrNull() ?: 0.0)`), because a half-typed number is
not a number.

## 3. ViewModel — `persist()` + field setters

```kotlin
class CustomerFormViewModel(
    private val getCustomers: GetCustomersUseCase,
    private val addCustomer: AddCustomerUseCase,
    private val updateCustomer: UpdateCustomerUseCase,
) : BaseFormViewModel<CustomerFormUiState>(CustomerFormUiState()) {

    fun init(mode: CustomerFormMode) {
        setState { copy(mode = mode) }
        if (mode is CustomerFormMode.Edit) hydrateForEdit(mode.customerId)
    }

    fun onName(v: String) = patch { copy(name = v) }
    fun onPhone(v: String) = patch { copy(phone = v) }

    override suspend fun persist(): Result<Unit> {
        val input = current.form.toInput()
        return when (val mode = current.mode) {
            is CustomerFormMode.Add  -> addCustomer(input).map { Unit }
            is CustomerFormMode.Edit -> updateCustomer(UpdateCustomerParam(mode.customerId, input))
        }
    }

    private fun patch(transform: CustomerFormFields.() -> CustomerFormFields) {
        setState { copy(form = form.transform()) }
    }
}
```

Edit forms hydrate through `launchResult(block = { … }, onSuccess = …,
onFailure = …)` and set **both** `form` and `baselineForm` so the page doesn't
open already claiming unsaved changes.

## 4. Screen

```kotlin
@Composable
fun CustomerFormScreen(
    customerId: String?,
    onBack: () -> Unit,
    viewModel: CustomerFormViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings
    RegisterUnsavedChanges(state.hasUnsavedChanges)

    LaunchedEffect(customerId) {
        viewModel.init(
            if (customerId.isNullOrBlank()) CustomerFormMode.Add else CustomerFormMode.Edit(customerId),
        )
    }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            snackbar.showToast(PharmToast.Success(s.commonSaved))
            onBack()
        }
    }

    CustomerFormContent(state = state, callbacks = CustomerFormCallbacks(…))
}
```

`RegisterUnsavedChanges(state.hasUnsavedChanges)` wires the leave-confirmation
guard. Success is a toast via `LocalPharmSnackbar`, not a state field.

## 5. Content — the sub-page layout

```kotlin
Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
    PharmListToolbar(
        title = if (state.isEdit) s.customersFormEditTitle else s.customersAddCta,
        onBack = callbacks.onBack,
        actions = {
            PharmSaveAction(
                saving = state.saving,
                canSubmit = state.canSubmit,
                onSubmit = callbacks.onSubmit,
                onInvalidSubmit = { validationRequested = true; nameFocusRequester.requestFocus() },
            )
        },
    )
    Column(
        modifier = Modifier
            .weight(1f)
            .then(pharmFormContentWidth())      // centred, capped at 768dp
            .imePadding()
            .verticalScroll(rememberScrollState())
            .pharmFormContentPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        if (state.loading) PharmCircularProgress(color = t.colors.accent)
        else CustomerFormInfoSection(form = state.form, callbacks = callbacks, showValidation = validationRequested)
    }
}
ErrorBottomSheet(message = state.errorState?.localizeCustomerForm(s), onDismiss = callbacks.onDismissError)
```

No bottom save bar, no inline Cancel — the back arrow is the way out.

`onInvalidSubmit` is what makes an invalid save useful: the button stays
pressable, and pressing it turns validation messages on and focuses the first
offending field. `validationRequested` is remembered on `state.mode` so
switching Add ↔ Edit resets it.

Sections go in their own file (`<X>FormInfoSection.kt`) once there is more than
one, each a `PharmFormCard(title) { … }` of `FormField(label, required, hint,
error) { PharmTextField(…) }`.

### FormField rules

- Static label above the input — never M3's floating `label = {}` slot.
- Validation text goes through `error =` on `FormField`, and the copy comes
  from `pharmStrings`.
- A conditional trailing icon must keep its slot: always render the button,
  gate the inner `Icon`, set `enabled = cond`. Otherwise the field jumps width.

## 6. Previews, DI, tests

- ≥3 `@Preview` in `PharmacyTheme { }`: empty / filled / saving.
- `features/<x>/…/di/<Feat>Module.kt`: `factoryOf(::CustomerFormViewModel)` —
  ViewModels only. A23 fails the build if a feature DI module imports a use
  case, observer or parser.
- Tests (`kmp-test`) must cover: `canSubmit` toggling with required fields,
  `submit()` reaching `saved`, `submit()` landing a typed error on failure,
  `submit()` no-opping when `canSubmit == false`, and `hasUnsavedChanges`
  being false right after an edit-mode hydrate.

## Verify

```bash
./gradlew :features:<feat>:jvmTest :composeApp:auditArchitecture
```
