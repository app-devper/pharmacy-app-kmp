package app.devper.pharm.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun SettingsStockTab(
    state: SettingsEditorUiState,
    editor: SettingsEditorCallbacks,
    showValidation: Boolean,
    focus: SettingsFocusRequesters,
) {
    val f = state.form
    val s = pharmStrings
    val lowThresholdError = if (showValidation && !f.stockLowThresholdValid) {
        s.validationMustBeNonNegative(s.settingsStockLowThresholdLabel)
    } else null
    val reorderDaysError = if (showValidation && !f.stockReorderDaysValid) {
        s.settingsStockRangeError(s.settingsStockReorderDays, 1, 365)
    } else null
    val reorderLookaheadError = if (showValidation && !f.stockReorderLookaheadValid) {
        s.settingsStockRangeError(s.settingsStockReorderLookahead, 1, 180)
    } else null
    val expiringDaysError = if (showValidation && !f.stockExpiringDaysValid) {
        s.settingsStockRangeError(s.settingsStockExpiringDays, 1, 365)
    } else null

    SettingsStockNumberField(
        label = s.settingsStockLowThresholdLabel,
        value = f.stockLowThreshold,
        onValueChange = editor.onStockLowThreshold,
        placeholder = "0",
        hint = if (lowThresholdError == null) s.settingsStockLowThresholdPlaceholder else null,
        error = lowThresholdError,
        focusRequester = focus.stockLowThreshold,
    )

    BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
        if (maxWidth >= PharmBreakpoint.FormTwoCol) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsStockNumberField(
                    label = s.settingsStockReorderDays,
                    value = f.stockReorderDays,
                    onValueChange = editor.onStockReorderDays,
                    placeholder = "30",
                    error = reorderDaysError,
                    focusRequester = focus.stockReorderDays,
                    modifier = Modifier.weight(1f),
                )
                SettingsStockNumberField(
                    label = s.settingsStockReorderLookahead,
                    value = f.stockReorderLookahead,
                    onValueChange = editor.onStockReorderLookahead,
                    placeholder = "14",
                    error = reorderLookaheadError,
                    focusRequester = focus.stockReorderLookahead,
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                SettingsStockNumberField(
                    label = s.settingsStockReorderDays,
                    value = f.stockReorderDays,
                    onValueChange = editor.onStockReorderDays,
                    placeholder = "30",
                    error = reorderDaysError,
                    focusRequester = focus.stockReorderDays,
                )
                SettingsStockNumberField(
                    label = s.settingsStockReorderLookahead,
                    value = f.stockReorderLookahead,
                    onValueChange = editor.onStockReorderLookahead,
                    placeholder = "14",
                    error = reorderLookaheadError,
                    focusRequester = focus.stockReorderLookahead,
                )
            }
        }
    }

    SettingsStockNumberField(
        label = s.settingsStockExpiringDays,
        value = f.stockExpiringDays,
        onValueChange = editor.onStockExpiringDays,
        placeholder = "60",
        error = expiringDaysError,
        focusRequester = focus.stockExpiringDays,
    )
}

@Composable
private fun SettingsStockNumberField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    error: String?,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier,
    hint: String? = null,
) {
    SettingsLabeledField(
        label = label,
        modifier = modifier,
        required = true,
        hint = hint,
        error = error,
    ) {
        SettingsFormField(
            value = value,
            onValueChange = onValueChange,
            placeholder = placeholder,
            keyboardType = KeyboardType.Number,
            isError = error != null,
            focusRequester = focusRequester,
        )
    }
}
