package app.devper.pharm.presentation.bulkimport

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.BulkImportRowError
import app.devper.pharm.domain.param.inventory.AddDrugParam
import app.devper.pharm.presentation.bulkimport.i18n.localizeBulkImport
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun BulkImportContent(
    state: BulkImportUiState,
    callbacks: BulkImportCallbacks = BulkImportCallbacks(),
) {
    val t = pharmTokens
    val s = pharmStrings
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        PharmListToolbar(
            actions = {
                PharmButton(
                    label = s.bulkImportDownloadTemplate,
                    onClick = callbacks.onDownloadTemplate,
                    variant = PharmButtonVariant.Outline,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Excel, contentDescription = null) },
                )
            },
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(t.shapes.lg)
                    .background(t.colors.surface)
                    .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
            BulkImportDropZone(onPickFile = callbacks.onPickFile)

            BulkImportJsonInput(
                value = state.text,
                onValueChange = callbacks.onJsonChange,
                parseError = state.parseErrorState?.localizeBulkImport(s),
            )

            state.previewCount?.let { count ->
                if (state.parseErrorState == null && state.result == null) {
                    BulkImportInfoBanner(
                        text = s.bulkImportValidatedReady(count),
                    )
                }
            }

            BulkImportActionRow(state = state, callbacks = callbacks)
        }

        state.result?.let { result ->
            BulkImportResultSummary(result = result)
        }

        if (state.rows.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(t.shapes.lg)
                    .background(t.colors.surface)
                    .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
            ) {
                BulkImportResultHeader(rows = state.rows)
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
                Box(modifier = Modifier.heightIn(min = 240.dp, max = 480.dp)) {
                    BulkImportResultTable(rows = state.rows)
                }
            }
        }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeBulkImport(s), onDismiss = callbacks.onDismissError)
}

@Composable
private fun BulkImportActionRow(
    state: BulkImportUiState,
    callbacks: BulkImportCallbacks,
) {
    val s = pharmStrings
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PharmButton(
            label = s.bulkImportValidateCta,
            onClick = callbacks.onPreview,
            variant = PharmButtonVariant.Outline,
            size = PharmButtonSize.Md,
            enabled = state.canSubmit,
        )
        PharmButton(
            onClick = callbacks.onSubmit,
            variant = PharmButtonVariant.Primary,
            size = PharmButtonSize.Md,
            enabled = state.canSubmit,
            modifier = Modifier.weight(1f),
        ) {
            if (state.submitting) {
                PharmCircularProgress(
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(18.dp),
                )
            } else {
                Text(text = s.bulkImportImportAllCta, style = PharmText.buttonMd)
            }
        }
        PharmButton(
            label = s.bulkImportClearCta,
            onClick = callbacks.onClear,
            variant = PharmButtonVariant.Ghost,
            size = PharmButtonSize.Md,
            enabled = !state.submitting,
        )
    }
}

@Composable
private fun BulkImportInfoBanner(text: String) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.infoBg)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Icon(
            imageVector = PharmIcons.Check,
            contentDescription = null,
            tint = t.colors.infoFg,
            modifier = Modifier.size(16.dp),
        )
        Text(text = text, style = PharmText.bodySm.copy(color = t.colors.infoFg))
    }
}

private val sampleParsed: List<AddDrugParam> = listOf(
    AddDrugParam(
        name = "พาราเซตามอล 500mg",
        genericName = "Paracetamol",
        unit = "เม็ด",
        sellPrice = Money(2.0),
        stock = Quantity(100),
    ),
    AddDrugParam(
        name = "อะม็อกซีซิลลิน 500mg",
        genericName = "Amoxicillin",
        unit = "แคปซูล",
        sellPrice = Money(5.0),
        stock = Quantity(200),
    ),
    AddDrugParam(
        name = "ลอราทาดีน 10mg",
        unit = "เม็ด",
        sellPrice = Money(3.0),
        stock = Quantity(80),
    ),
    AddDrugParam(
        name = "ไอบูโพรเฟน 400mg",
        unit = "เม็ด",
        sellPrice = Money(4.0),
        stock = Quantity(60),
    ),
)

private val sampleResultMixed = BulkImportResult(
    imported = 3,
    errors = listOf(
        BulkImportRowError(row = 3, name = "ลอราทาดีน 10mg", message = "barcode ซ้ำกับยาที่มีอยู่"),
    ),
)

@Preview
@Composable
private fun BulkImportContent_Empty_Preview() {
    PharmacyTheme {
        BulkImportContent(state = BulkImportUiState())
    }
}

@Preview
@Composable
private fun BulkImportContent_Parsed_Preview() {
    PharmacyTheme {
        BulkImportContent(
            state = BulkImportUiState(
                text = BULK_IMPORT_SAMPLE_JSON,
                parsed = sampleParsed,
                previewCount = sampleParsed.size,
            ),
        )
    }
}

@Preview
@Composable
private fun BulkImportContent_Submitting_Preview() {
    PharmacyTheme {
        BulkImportContent(
            state = BulkImportUiState(
                text = BULK_IMPORT_SAMPLE_JSON,
                parsed = sampleParsed,
                previewCount = sampleParsed.size,
                submitting = true,
            ),
        )
    }
}

@Preview
@Composable
private fun BulkImportContent_SubmittedMixed_Preview() {
    PharmacyTheme {
        BulkImportContent(
            state = BulkImportUiState(
                text = BULK_IMPORT_SAMPLE_JSON,
                parsed = sampleParsed,
                previewCount = sampleParsed.size,
                result = sampleResultMixed,
            ),
        )
    }
}
