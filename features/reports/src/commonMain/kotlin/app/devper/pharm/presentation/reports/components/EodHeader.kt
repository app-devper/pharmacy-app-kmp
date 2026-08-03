package app.devper.pharm.presentation.reports.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.reports.EodCallbacks
import app.devper.pharm.presentation.reports.EodCloseButton
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmSearchAction
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
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
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
                    trailingSlot = {
                        PharmSearchAction(
                            onClick = callbacks.onApplyDate,
                            enabled = !closing,
                            searching = loading,
                        )
                    },
                    isError = dateError != null,
                    enabled = !loading && !closing,
                )
            }
            EodCloseButton(
                closed = closed,
                enabled = !loading && !closing && hasReport,
                onClick = callbacks.onRequestClose,
            )
        }
    }
}
