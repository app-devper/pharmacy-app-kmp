package app.devper.pharm.presentation.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.reports.EodCallbacks
import app.devper.pharm.presentation.reports.EodCloseButton
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.pharmTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun EodHeader(
    date: String,
    dateError: String?,
    loading: Boolean,
    closing: Boolean,
    closed: Boolean,
    hasReport: Boolean,
    callbacks: EodCallbacks,
) {
    val t = pharmTokens
    val s = pharmStrings
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(t.colors.surface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            itemVerticalAlignment = Alignment.Bottom,
        ) {
            FormField(
                label = s.reportsEodDate,
                hint = s.reportsDatePlaceholder,
                error = dateError,
                required = true,
                modifier = Modifier.weight(1f),
            ) {
                PharmTextField(
                    value = date,
                    onValueChange = callbacks.onDateChange,
                    placeholder = "YYYY-MM-DD…",
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Search,
                    onImeAction = callbacks.onApplyDate,
                    isError = dateError != null,
                    enabled = !loading && !closing,
                )
            }
            PharmButton(
                label = s.commonSearch,
                onClick = callbacks.onApplyDate,
                variant = PharmButtonVariant.Secondary,
                size = PharmButtonSize.Md,
                loading = loading,
                enabled = !closing,
            )
            EodCloseButton(
                closed = closed,
                enabled = !loading && !closing && hasReport,
                onClick = callbacks.onRequestClose,
            )
        }
    }
}
