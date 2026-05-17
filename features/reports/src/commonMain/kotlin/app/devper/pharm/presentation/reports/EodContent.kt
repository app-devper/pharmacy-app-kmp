package app.devper.pharm.presentation.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.domain.model.SaleSummary
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens
import kotlin.math.abs
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun EodContent(
    state: EodUiState,
    callbacks: EodCallbacks = EodCallbacks(),
) {
    val t = pharmTokens
    val report = state.report

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        EodHeader(
            date = state.date,
            loading = state.loading,
            closed = state.closed,
            hasReport = report != null,
            callbacks = callbacks,
        )

        when {
            state.loading && report == null ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = t.colors.accent)
                }

            report == null -> EmptyEod()

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item("summary") { EodSummaryCards(report) }
                item("balance") { EodBalanceCard(report) }
                if (state.closed) item("closed") { EodClosedReceiptCard(report = report, onPrint = callbacks.onPrint) }
                item("bills-header") { EodBillsHeader(count = report.billCount) }
                items(report.bills, key = { it.id }) { bill -> EodBillRow(bill = bill) }
            }
        }
    }

    EodConfirmCloseModal(
        open = state.confirmClose,
        report = report,
        onConfirm = callbacks.onConfirmClose,
        onCancel = callbacks.onCancelClose,
    )

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun EodHeader(
    date: String,
    loading: Boolean,
    closed: Boolean,
    hasReport: Boolean,
    callbacks: EodCallbacks,
) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = "ปิดยอดสิ้นวัน", style = PharmText.h1)
                Text(
                    text = "สรุปยอดขาย / ส่วนลด / เงินสดของวัน — ยืนยันก่อนปิดรอบ",
                    style = PharmText.meta.copy(color = t.colors.fgMuted),
                )
            }
            if (closed) {
                PharmBadge(text = "ปิดแล้ว", tone = PharmBadgeTone.Green)
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            FormField(
                label = "วันที่",
                hint = "รูปแบบ YYYY-MM-DD (ว่าง = วันนี้)",
                modifier = Modifier.weight(1f),
            ) {
                PharmTextField(
                    value = date,
                    onValueChange = callbacks.onDateChange,
                    placeholder = "YYYY-MM-DD",
                    keyboardType = KeyboardType.Number,
                )
            }
            PharmButton(
                label = "ค้นหา",
                onClick = callbacks.onApplyDate,
                variant = PharmButtonVariant.Secondary,
                size = PharmButtonSize.Md,
                enabled = !loading,
            )
            EodCloseButton(
                closed = closed,
                enabled = !loading && hasReport,
                onClick = callbacks.onRequestClose,
            )
        }
    }
}

@Composable
private fun EodBalanceCard(report: EodReport) {
    val t = pharmTokens
    val drift = report.netCash - report.totalSales
    val balanced = abs(drift) < 0.01
    val bg = if (balanced) t.colors.successBg else t.colors.dangerBg
    val fg = if (balanced) t.colors.successFg else t.colors.dangerFg
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(bg, t.shapes.lg)
            .border(1.dp, fg.copy(alpha = 0.4f), t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = if (balanced) PharmIcons.Check else PharmIcons.Warning,
            contentDescription = null,
            tint = fg,
            modifier = Modifier.size(20.dp),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (balanced) "ลิ้นชักตรงกับยอดขาย" else "ลิ้นชักไม่ตรงกับยอดขาย",
                style = PharmText.h3.copy(color = fg),
            )
            Text(
                text = "เงินเข้าลิ้นชัก ${fmtBaht(report.netCash)} · ยอดขาย ${fmtBaht(report.totalSales)}",
                style = PharmText.meta.copy(color = fg),
            )
        }
        if (!balanced) {
            Text(
                text = (if (drift > 0) "+${fmtBaht(drift)}" else fmtBaht(drift)),
                style = PharmText.total.copy(color = fg),
            )
        }
    }
}

@Composable
private fun EodClosedReceiptCard(report: EodReport, onPrint: () -> Unit) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.accent, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = PharmIcons.Check,
                contentDescription = null,
                tint = t.colors.successFg,
                modifier = Modifier.size(18.dp),
            )
            Text(
                text = "ปิดรอบ EOD เรียบร้อย — วันที่ ${report.date.ifBlank { "วันนี้" }}",
                style = PharmText.h2,
                modifier = Modifier.weight(1f),
            )
            PharmButton(
                label = "พิมพ์",
                onClick = onPrint,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = {
                    Icon(
                        imageVector = PharmIcons.Print,
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                    )
                },
            )
        }
        EodReceiptLine(label = "ยอดขายสุทธิ", value = fmtBaht(report.totalSales))
        EodReceiptLine(label = "จำนวนบิล", value = "${report.billCount} บิล")
        EodReceiptLine(label = "ส่วนลดรวม", value = fmtBaht(report.totalDiscount))
        EodReceiptLine(label = "รับเงิน", value = fmtBaht(report.totalReceived))
        EodReceiptLine(label = "ทอนเงิน", value = fmtBaht(report.totalChange))
        Box(modifier = Modifier.fillMaxWidth().padding(top = 4.dp)) {
            EodReceiptLine(label = "เงินเข้าลิ้นชัก", value = fmtBaht(report.netCash), bold = true)
        }
    }
}

@Composable
private fun EodReceiptLine(label: String, value: String, bold: Boolean = false) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(text = label, style = PharmText.meta.copy(color = t.colors.fg2))
        Text(
            text = value,
            style = if (bold) PharmText.total else PharmText.bodySm.copy(color = t.colors.fg1),
        )
    }
}

@Composable
private fun EodBillsHeader(count: Int) {
    val t = pharmTokens
    Text(
        text = "บิลในวัน · $count รายการ",
        style = PharmText.h3.copy(color = t.colors.fg1),
    )
}

@Composable
private fun EodBillRow(bill: SaleSummary) {
    val t = pharmTokens
    val totalColor = if (bill.voided) t.colors.fgMuted else t.colors.price
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.surface, t.shapes.md)
            .border(1.dp, t.colors.borderSubtle, t.shapes.md)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = bill.billNo.ifBlank { bill.id.take(8) },
                style = PharmText.bodySm,
            )
            Text(
                text = bill.soldAt.take(19).replace('T', ' '),
                style = PharmText.micro.copy(color = t.colors.fgMuted),
            )
        }
        if (bill.voided) {
            PharmBadge(text = "ยกเลิก", tone = PharmBadgeTone.Gray)
        }
        Text(
            text = fmtBaht(bill.total),
            style = PharmText.price.copy(color = totalColor),
        )
    }
}

@Composable
private fun EmptyEod() {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .fillMaxSize()
            .clip(t.shapes.lg)
            .background(t.colors.surface, t.shapes.lg)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(32.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "ไม่มีข้อมูล", style = PharmText.h2)
            Text(
                text = "ลองเลือกวันที่อื่นเพื่อดูยอดขาย",
                style = PharmText.meta.copy(color = t.colors.fgMuted),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

private val sampleBills = listOf(
    SaleSummary("s1", "B260517-001", "นาย ก", 420.0, 0.0, "2026-05-17T09:15:00", false),
    SaleSummary("s2", "B260517-002", "", 180.0, 20.0, "2026-05-17T10:45:00", false),
    SaleSummary("s3", "B260517-003", "นาง ข", 950.0, 0.0, "2026-05-17T12:02:00", true),
    SaleSummary("s4", "B260517-004", "ร้านยาฝั่งตรงข้าม", 2350.0, 50.0, "2026-05-17T13:24:00", false),
)

private val sampleReport = EodReport(
    date = "2026-05-17",
    billCount = 4,
    totalSales = 3850.0,
    totalDiscount = 70.0,
    totalReceived = 4000.0,
    totalChange = 150.0,
    netCash = 3850.0,
    bills = sampleBills,
)

private val unbalancedReport = sampleReport.copy(netCash = 3920.0)

@Preview
@Composable
private fun EodContent_Open_Preview() {
    PharmacyTheme {
        EodContent(state = EodUiState(date = "2026-05-17", report = sampleReport))
    }
}

@Preview
@Composable
private fun EodContent_Closed_Preview() {
    PharmacyTheme {
        EodContent(state = EodUiState(date = "2026-05-17", report = sampleReport, closed = true))
    }
}

@Preview
@Composable
private fun EodContent_Unbalanced_Preview() {
    PharmacyTheme {
        EodContent(state = EodUiState(date = "2026-05-17", report = unbalancedReport))
    }
}

@Preview
@Composable
private fun EodContent_Confirm_Preview() {
    PharmacyTheme {
        EodContent(state = EodUiState(date = "2026-05-17", report = sampleReport, confirmClose = true))
    }
}

@Preview
@Composable
private fun EodContent_Loading_Preview() {
    PharmacyTheme {
        EodContent(state = EodUiState(loading = true))
    }
}

@Preview
@Composable
private fun EodContent_Empty_Preview() {
    PharmacyTheme {
        EodContent(state = EodUiState())
    }
}
