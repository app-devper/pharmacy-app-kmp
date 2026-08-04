package app.devper.pharm.presentation.labels

import app.devper.pharm.ui.components.PharmBreakpoint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.presentation.labels.components.LabelDrugPicker
import app.devper.pharm.presentation.labels.components.LabelFieldEditor
import app.devper.pharm.presentation.labels.components.LabelPreviewPane
import app.devper.pharm.presentation.labels.components.LabelPrintToolbar
import app.devper.pharm.presentation.labels.i18n.localizeLabels
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LabelPrintContent(
    state: LabelPrintUiState,
    callbacks: LabelPrintCallbacks = LabelPrintCallbacks(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        PharmListToolbar(
            subtitle = pharmStrings.labelsSubtitle,
            modifier = Modifier.widthIn(max = t.dimens.dashboardContentMaxWidth),
        )
        BoxWithConstraints(
            modifier = Modifier
                .widthIn(max = t.dimens.dashboardContentMaxWidth)
                .fillMaxWidth()
                .weight(1f)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
        ) {
            val sideBySide = maxWidth >= PharmBreakpoint.Medium
            if (sideBySide) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    LabelDrugPicker(
                        state = state,
                        onQueryChange = callbacks.onQueryChange,
                        onAddDrug = callbacks.onAddDrug,
                        modifier = Modifier.weight(2f).fillMaxHeight(),
                    )
                    EditorPane(
                        state = state,
                        callbacks = callbacks,
                        modifier = Modifier.weight(3f).fillMaxHeight(),
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    LabelDrugPicker(
                        state = state,
                        onQueryChange = callbacks.onQueryChange,
                        onAddDrug = callbacks.onAddDrug,
                        modifier = Modifier.fillMaxWidth().weight(2f),
                    )
                    EditorPane(
                        state = state,
                        callbacks = callbacks,
                        modifier = Modifier.fillMaxWidth().weight(3f),
                    )
                }
            }
        }
    }

    state.errorState?.let { err ->
        ErrorBottomSheet(message = err.localizeLabels(pharmStrings), onDismiss = callbacks.onDismissError)
    }
    PharmModal(
        open = state.confirmClear,
        onDismiss = callbacks.onCancelClearAll,
        title = pharmStrings.labelsClearTitle,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = pharmStrings.commonCancel,
                onClick = callbacks.onCancelClearAll,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = pharmStrings.labelsClearConfirm,
                onClick = callbacks.onConfirmClearAll,
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(text = pharmStrings.labelsClearSubtitle, style = PharmText.body)
    }
}

@Composable
private fun EditorPane(
    state: LabelPrintUiState,
    callbacks: LabelPrintCallbacks,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        LabelPrintToolbar(
            size = state.size,
            totalCopies = state.totalCopies,
            canPrint = state.canPrint,
            canClear = state.canClear,
            printing = state.printing,
            onSizeChange = callbacks.onSizeChange,
            onClearAll = callbacks.onAskClearAll,
            onPrint = callbacks.onPrint,
        )
        LabelFieldEditor(
            lines = state.lines,
            onRemoveLine = callbacks.onRemoveLine,
            onChangeCopies = callbacks.onChangeCopies,
            onChangeBarcode = callbacks.onChangeBarcode,
            onToggleIncludePrice = callbacks.onToggleIncludePrice,
            modifier = Modifier.fillMaxWidth().weight(1f),
        )
        LabelPreviewPane(
            size = state.size,
            line = state.previewLine,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private val previewLines = listOf(
    LabelLine(
        drugId = "d1",
        drugName = "พาราเซตามอล 500mg",
        lotNumber = "A12345",
        barcode = "8850001234567",
        price = 2.0,
        includePrice = true,
        copies = 3,
    ),
    LabelLine(
        drugId = "d2",
        drugName = "อะม็อกซีซิลลิน 250mg",
        lotNumber = "B67890",
        barcode = "8850007654321",
        price = 5.0,
        includePrice = false,
        copies = 1,
    ),
)

@Preview
@Composable
private fun LabelPrintContent_Loaded_Preview() {
    PharmacyTheme {
        LabelPrintContent(
            state = LabelPrintUiState(lines = previewLines, size = LabelSize.Small),
        )
    }
}

@Preview
@Composable
private fun LabelPrintContent_Empty_Preview() {
    PharmacyTheme {
        LabelPrintContent(state = LabelPrintUiState())
    }
}

@Preview
@Composable
private fun LabelPrintContent_Printing_Preview() {
    PharmacyTheme {
        LabelPrintContent(
            state = LabelPrintUiState(lines = previewLines, printing = true),
        )
    }
}
