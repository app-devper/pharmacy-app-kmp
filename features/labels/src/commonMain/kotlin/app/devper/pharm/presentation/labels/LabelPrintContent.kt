package app.devper.pharm.presentation.labels

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.labels.components.LabelDrugPicker
import app.devper.pharm.presentation.labels.components.LabelFieldEditor
import app.devper.pharm.presentation.labels.components.LabelPreviewPane
import app.devper.pharm.presentation.labels.components.LabelPrintToolbar
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun LabelPrintContent(
    state: LabelPrintUiState,
    callbacks: LabelPrintCallbacks = LabelPrintCallbacks(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
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

            Column(
                modifier = Modifier.weight(3f).fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                LabelPrintToolbar(
                    size = state.size,
                    totalCopies = state.totalCopies,
                    canPrint = state.canPrint,
                    printing = state.printing,
                    onSizeChange = callbacks.onSizeChange,
                    onClearAll = callbacks.onClearAll,
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
    }

    if (state.message != null) {
        ErrorBottomSheet(message = state.message, onDismiss = callbacks.onDismissMessage, title = "พิมพ์สำเร็จ")
    }
    if (state.error != null) {
        ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
    }
}
