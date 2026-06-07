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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.BulkImportRowError
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
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
    val scroll = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage),
    ) {
        PharmListToolbar(
            title = "นำเข้ายาด้วย JSON",
            subtitle = "อัปโหลดไฟล์ JSON หรือวางข้อความเพื่อสร้างยาทีเดียวหลายรายการ",
            actions = {
                PharmButton(
                    label = "ดาวน์โหลด Template",
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
                parseError = state.parseError,
            )

            state.previewCount?.let { count ->
                if (state.parseError == null && state.result == null) {
                    BulkImportInfoBanner(
                        text = "ตรวจสอบแล้ว — พบ $count รายการ พร้อมนำเข้า",
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

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun BulkImportActionRow(
    state: BulkImportUiState,
    callbacks: BulkImportCallbacks,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PharmButton(
            label = "ตรวจสอบ",
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
                Text(text = "นำเข้าทั้งหมด", style = PharmText.buttonMd)
            }
        }
        PharmButton(
            label = "ล้าง",
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

@Composable
private fun BulkImportResultSummary(result: BulkImportResult) {
    val t = pharmTokens
    val bg = if (result.hasErrors) t.colors.warningBg else t.colors.successBg
    val fg = if (result.hasErrors) t.colors.warningFg else t.colors.successFg
    val title = if (result.hasErrors) "นำเข้าบางส่วน" else "นำเข้าสำเร็จทั้งหมด"
    val icon = if (result.hasErrors) PharmIcons.Warning else PharmIcons.Check

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(bg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = fg, modifier = Modifier.size(20.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = PharmText.h3.copy(color = fg, fontWeight = FontWeight.SemiBold),
            )
            Text(
                text = "บันทึก ${result.imported}/${result.totalAttempted} รายการ",
                style = PharmText.bodySm.copy(color = fg).tabular(),
            )
        }
    }
}

@Composable
private fun BulkImportResultHeader(rows: List<BulkImportRow>) {
    val t = pharmTokens
    val done = rows.count { it.status == BulkImportRowStatus.Done }
    val failed = rows.count { it.status == BulkImportRowStatus.Failed }
    val pending = rows.count { it.status == BulkImportRowStatus.Pending }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "ผลการนำเข้า · ${rows.size} รายการ",
            style = PharmText.h3,
            modifier = Modifier.weight(1f),
        )
        if (pending > 0) {
            BulkImportCountChip(label = "พร้อมนำเข้า", value = pending, color = t.colors.warningFg)
        }
        if (done > 0) {
            BulkImportCountChip(label = "สำเร็จ", value = done, color = t.colors.successFg)
        }
        if (failed > 0) {
            BulkImportCountChip(label = "ผิดพลาด", value = failed, color = t.colors.dangerFg)
        }
    }
}

@Composable
private fun BulkImportCountChip(label: String, value: Int, color: androidx.compose.ui.graphics.Color) {
    val t = pharmTokens
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = "$value",
            style = PharmText.bodySm.copy(color = color, fontWeight = FontWeight.SemiBold).tabular(),
        )
        Text(text = label, style = PharmText.micro.copy(color = t.colors.fg3))
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
