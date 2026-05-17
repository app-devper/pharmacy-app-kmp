package app.devper.pharm.presentation.imports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ImportsListContent(
    state: ImportsListUiState,
    callbacks: ImportsListCallbacks = ImportsListCallbacks(),
) {
    val t = pharmTokens
    val visible = state.filtered
    val searching = state.query.isNotBlank()

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
            ImportsListToolbar(
                query = state.query,
                draftCount = state.draftCount,
                callbacks = callbacks,
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            ImportsResultLine(visible = visible.size, total = state.orders.size, searching = searching)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.orders.isEmpty() ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = t.colors.accent)
                    }
                else -> ImportsListTable(
                    orders = visible,
                    callbacks = callbacks,
                    emptySearching = searching,
                )
            }
        }
    }

    state.pendingConfirm?.let { pending ->
        ConfirmImportModal(
            order = pending,
            busy = state.busy,
            onConfirm = callbacks.onConfirmConfirm,
            onDismiss = callbacks.onCancelConfirm,
        )
    }

    state.pendingDelete?.let { pending ->
        DeleteImportModal(
            order = pending,
            busy = state.busy,
            onConfirm = callbacks.onConfirmDelete,
            onDismiss = callbacks.onCancelDelete,
        )
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun ImportsResultLine(visible: Int, total: Int, searching: Boolean) {
    val t = pharmTokens
    val text = if (searching) "พบ $visible ใบ จากทั้งหมด $total"
    else "ทั้งหมด $total ใบ"
    Text(
        text = text,
        style = PharmText.micro.copy(color = t.colors.fg3),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
    )
}

@Composable
private fun ConfirmImportModal(
    order: PurchaseOrderSummary,
    busy: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = "ยืนยันรับเข้า?",
        subtitle = order.docNo,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = "ยกเลิก",
                onClick = onDismiss,
                variant = PharmButtonVariant.Outline,
                enabled = !busy,
            )
            PharmButton(
                label = "ยืนยันรับ",
                onClick = onConfirm,
                variant = PharmButtonVariant.Primary,
                enabled = !busy,
            )
        },
    ) {
        Text(
            text = "ยืนยันรับสินค้าเข้าสต็อกตามใบนำเข้านี้หรือไม่ — รายการล็อตจะถูกบันทึกและไม่สามารถย้อนกลับได้",
            style = PharmText.body,
        )
    }
}

@Composable
private fun DeleteImportModal(
    order: PurchaseOrderSummary,
    busy: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = "ลบใบนำเข้า?",
        subtitle = order.docNo,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = "ยกเลิก",
                onClick = onDismiss,
                variant = PharmButtonVariant.Outline,
                enabled = !busy,
            )
            PharmButton(
                label = "ลบ",
                onClick = onConfirm,
                variant = PharmButtonVariant.Danger,
                enabled = !busy,
            )
        },
    ) {
        Text(
            text = "ต้องการลบใบนำเข้านี้ใช่หรือไม่ — ใบที่ยังไม่ได้ยืนยันเท่านั้นที่ลบได้",
            style = PharmText.body,
        )
    }
}

private val sampleOrders = listOf(
    PurchaseOrderSummary(
        id = "1",
        docNo = "IMP-260516-002",
        supplier = "บ. ฟาร์มาแกรนด์ จำกัด",
        invoiceNo = "INV-26050-014",
        receiveDate = "2026-05-16",
        itemCount = 8,
        totalCost = 32400.0,
        status = PurchaseOrderStatus.Confirmed,
        notes = "",
        createdAt = "2026-05-16T09:00:00",
        confirmedAt = "2026-05-16T10:00:00",
    ),
    PurchaseOrderSummary(
        id = "2",
        docNo = "IMP-260516-001",
        supplier = "หจก. สมุนไพรไทย",
        invoiceNo = "INV-26050-072",
        receiveDate = "2026-05-16",
        itemCount = 3,
        totalCost = 5400.0,
        status = PurchaseOrderStatus.Confirmed,
        notes = "",
        createdAt = "2026-05-16T11:00:00",
        confirmedAt = "2026-05-16T12:00:00",
    ),
    PurchaseOrderSummary(
        id = "3",
        docNo = "IMP-260514-001",
        supplier = "บ. ไทยฟาร์มา จำกัด",
        invoiceNo = "INV-26050-027",
        receiveDate = "2026-05-14",
        itemCount = 5,
        totalCost = 19800.0,
        status = PurchaseOrderStatus.Confirmed,
        notes = "",
        createdAt = "2026-05-14T09:00:00",
        confirmedAt = "2026-05-14T10:00:00",
    ),
    PurchaseOrderSummary(
        id = "4",
        docNo = "IMP-260513-001",
        supplier = "บ. เฮลท์ตี้ดิสตริบิวเตอร์",
        invoiceNo = "INV-26050-041",
        receiveDate = "2026-05-13",
        itemCount = 12,
        totalCost = 48700.0,
        status = PurchaseOrderStatus.Draft,
        notes = "",
        createdAt = "2026-05-13T09:00:00",
        confirmedAt = null,
    ),
    PurchaseOrderSummary(
        id = "5",
        docNo = "IMP-260510-002",
        supplier = "บ. ฟาร์มาแกรนด์ จำกัด",
        invoiceNo = "INV-26050-008",
        receiveDate = "2026-05-10",
        itemCount = 4,
        totalCost = 9360.0,
        status = PurchaseOrderStatus.Confirmed,
        notes = "",
        createdAt = "2026-05-10T09:00:00",
        confirmedAt = "2026-05-10T10:00:00",
    ),
)

@Preview
@Composable
private fun ImportsListContent_Loaded_Preview() {
    PharmacyTheme {
        ImportsListContent(state = ImportsListUiState(orders = sampleOrders))
    }
}

@Preview
@Composable
private fun ImportsListContent_Empty_Preview() {
    PharmacyTheme {
        ImportsListContent(state = ImportsListUiState(orders = emptyList()))
    }
}

@Preview
@Composable
private fun ImportsListContent_Loading_Preview() {
    PharmacyTheme {
        ImportsListContent(state = ImportsListUiState(loading = true))
    }
}
