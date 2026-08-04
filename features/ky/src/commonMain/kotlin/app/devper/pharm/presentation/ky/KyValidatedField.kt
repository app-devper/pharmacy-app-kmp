package app.devper.pharm.presentation.ky

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.KeyboardType
import app.devper.pharm.domain.validation.Check
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings

internal enum class KyValidationRule {
    Required,
    Date,
    PositiveInt,
    NonNegativeNumber,
    OptionalNonNegativeInt,
    OptionalNonNegativeNumber,
}

internal class KyCommonFocusRequesters {
    val date = FocusRequester()
    val drugName = FocusRequester()
    val unit = FocusRequester()
    val qty = FocusRequester()

    fun requestFirstInvalid(
        date: String,
        drugName: String,
        unit: String,
        qty: String,
    ): Boolean = when {
        !Check.localDate(date) -> this.date.requestFocus()
        drugName.isBlank() -> this.drugName.requestFocus()
        unit.isBlank() -> this.unit.requestFocus()
        !Check.positiveInt(qty) -> this.qty.requestFocus()
        else -> false
    }
}

@Composable
internal fun rememberKyCommonFocusRequesters(): KyCommonFocusRequesters =
    remember { KyCommonFocusRequesters() }

@Composable
internal fun KyValidatedField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    showValidation: Boolean,
    focusRequester: FocusRequester,
    rule: KyValidationRule,
    keyboardType: KeyboardType = KeyboardType.Text,
) {
    val s = pharmStrings
    val error = kyValidationError(
        value = value,
        label = label,
        rule = rule,
        showValidation = showValidation,
        s = s,
    )
    val required = rule != KyValidationRule.OptionalNonNegativeInt &&
        rule != KyValidationRule.OptionalNonNegativeNumber
    FormField(label = label, required = required, error = error) {
        PharmTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardType = keyboardType,
            isError = error != null,
            modifier = Modifier.focusRequester(focusRequester),
        )
    }
}

private fun kyValidationError(
    value: String,
    label: String,
    rule: KyValidationRule,
    showValidation: Boolean,
    s: PharmStrings,
): String? {
    if (value.isBlank()) {
        val optional = rule == KyValidationRule.OptionalNonNegativeInt ||
            rule == KyValidationRule.OptionalNonNegativeNumber
        return if (!optional && showValidation) s.validationRequired(label) else null
    }
    return when (rule) {
        KyValidationRule.Required -> null
        KyValidationRule.Date -> if (Check.localDate(value)) null else s.validationInvalidDate(label)
        KyValidationRule.PositiveInt -> when {
            value.toIntOrNull() == null -> s.validationNotANumber(label)
            !Check.positiveInt(value) -> s.validationMustBePositive(label)
            else -> null
        }
        KyValidationRule.NonNegativeNumber,
        KyValidationRule.OptionalNonNegativeNumber,
        -> when {
            value.toDoubleOrNull() == null -> s.validationNotANumber(label)
            !Check.nonNegativeDouble(value) -> s.validationMustBeNonNegative(label)
            else -> null
        }
        KyValidationRule.OptionalNonNegativeInt -> when {
            value.toIntOrNull() == null -> s.validationNotANumber(label)
            value.toInt() < 0 -> s.validationMustBeNonNegative(label)
            else -> null
        }
    }
}
