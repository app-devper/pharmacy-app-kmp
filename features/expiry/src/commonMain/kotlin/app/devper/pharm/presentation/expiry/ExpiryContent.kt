package app.devper.pharm.presentation.expiry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffFailure
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmListSkeleton

@Composable
fun ExpiryContent(
    state: ExpiryUiState,
    callbacks: ExpiryCallbacks = ExpiryCallbacks(),
) {
    val t = pharmTokens

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            ExpiryToolbar(
                window = state.window,
                selectedCount = state.totalSelected,
                writingOff = state.writingOff,
                callbacks = callbacks,
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            ExpiryResultLine(count = state.lots.size, totalRemaining = state.totalRemaining)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.lots.isEmpty() ->
                    PharmListSkeleton(modifier = Modifier.fillMaxSize())
                else -> ExpiryTable(
                    lots = state.lots,
                    selected = state.selected,
                    allSelected = state.allSelected,
                    callbacks = callbacks,
                )
            }
        }
    }

    WriteoffConfirmDialog(
        open = state.confirmDialog,
        count = state.totalSelected,
        writingOff = state.writingOff,
        onConfirm = callbacks.onConfirmWriteoff,
        onDismiss = callbacks.onCancelWriteoff,
    )

    state.writeoffResult?.let { result ->
        WriteoffResultDialog(result = result, onDismiss = callbacks.onDismissResult)
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun WriteoffConfirmDialog(
    open: Boolean,
    count: Int,
    writingOff: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PharmModal(
        open = open,
        onDismiss = onDismiss,
        title = "ตัดจำหน่ายล็อต?",
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = "ยกเลิก",
                onClick = onDismiss,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
                enabled = !writingOff,
            )
            PharmButton(
                label = "ตัดจำหน่าย",
                onClick = onConfirm,
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
                enabled = !writingOff,
            )
        },
    ) {
        Text(
            text = "ระบบจะลบ $count ล็อต และลด stock ตาม remaining ของแต่ละล็อต — " +
                "บันทึกการตัดจำหน่ายไว้สำหรับตรวจสอบ",
            style = PharmText.body,
        )
    }
}

@Composable
private fun WriteoffResultDialog(result: WriteoffResult, onDismiss: () -> Unit) {
    val t = pharmTokens
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = if (result.hasFailures) "ตัดจำหน่ายบางส่วน" else "ตัดจำหน่ายสำเร็จ",
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = "ปิด",
                onClick = onDismiss,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(
                text = "บันทึก ${result.writtenOff}/${result.totalAttempted} ล็อต",
                style = PharmText.body,
            )
            if (result.hasFailures) {
                Text(
                    text = "${result.failures.size} ล็อตล้มเหลว — กรุณาลองใหม่",
                    style = PharmText.meta.copy(color = t.colors.dangerFg),
                )
                result.failures.take(3).forEach { f ->
                    Text(
                        text = "• ${f.message}",
                        style = PharmText.micro.copy(color = t.colors.fg3),
                    )
                }
                if (result.failures.size > 3) {
                    Text(
                        text = "(+${result.failures.size - 3} อื่นๆ)",
                        style = PharmText.micro.copy(color = t.colors.fg3),
                    )
                }
            }
        }
    }
}

private val sampleLots = listOf(
    ExpiringLot(
        id = "L001", drugId = "D001", drugName = "อะม็อกซีซิลลิน 500mg",
        lotNumber = "AMX-25011", expiryDate = "2026-06-10", remaining = 120, daysLeft = 24,
    ),
    ExpiringLot(
        id = "L002", drugId = "D002", drugName = "ลอราทาดีน 10mg",
        lotNumber = "LRT-24109", expiryDate = "2026-06-15", remaining = 240, daysLeft = 29,
    ),
    ExpiringLot(
        id = "L003", drugId = "D003", drugName = "ออเมพราโซล 20mg",
        lotNumber = "OMP-25003", expiryDate = "2026-06-28", remaining = 16, daysLeft = 42,
    ),
    ExpiringLot(
        id = "L004", drugId = "D004", drugName = "ดิเฟนไฮดรามีน ไซรัป",
        lotNumber = "DPH-25008", expiryDate = "2026-07-12", remaining = 9, daysLeft = 56,
    ),
    ExpiringLot(
        id = "L005", drugId = "D005", drugName = "ฟ้าทะลายโจร แคปซูล",
        lotNumber = "FTJ-25002", expiryDate = "2026-08-05", remaining = 38, daysLeft = 80,
    ),
    ExpiringLot(
        id = "L006", drugId = "D006", drugName = "พาราเซตามอล 500mg",
        lotNumber = "PCM-24123", expiryDate = "2026-05-24", remaining = 42, daysLeft = 7,
    ),
    ExpiringLot(
        id = "L007", drugId = "D007", drugName = "ไอบูโพรเฟน 400mg",
        lotNumber = "IBU-24087", expiryDate = "2026-05-10", remaining = 8, daysLeft = -7,
    ),
)

@Preview
@Composable
private fun ExpiryContent_Loaded_Preview() {
    PharmacyTheme {
        ExpiryContent(state = ExpiryUiState(lots = sampleLots))
    }
}

@Preview
@Composable
private fun ExpiryContent_WithSelection_Preview() {
    PharmacyTheme {
        ExpiryContent(
            state = ExpiryUiState(
                lots = sampleLots,
                selected = setOf("L001", "L006", "L007"),
            ),
        )
    }
}

@Preview
@Composable
private fun ExpiryContent_Empty_Preview() {
    PharmacyTheme {
        ExpiryContent(state = ExpiryUiState(lots = emptyList()))
    }
}

@Preview
@Composable
private fun ExpiryContent_Loading_Preview() {
    PharmacyTheme {
        ExpiryContent(state = ExpiryUiState(loading = true))
    }
}
