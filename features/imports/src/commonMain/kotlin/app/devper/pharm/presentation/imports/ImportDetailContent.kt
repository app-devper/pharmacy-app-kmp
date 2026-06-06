package app.devper.pharm.presentation.imports

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.PurchaseOrder
import app.devper.pharm.domain.model.PurchaseOrderItem
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.format.formatBaht
import app.devper.pharm.ui.format.formatBahtCurrency
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.theme.tabular
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ImportDetailContent(
    state: ImportDetailUiState,
    callbacks: ImportDetailCallbacks,
) {
    val t = pharmTokens

    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        Row(
            modifier = Modifier.fillMaxWidth().background(t.colors.surface).padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.clip(t.shapes.sm).clickable(role = Role.Button, onClick = callbacks.onBack).defaultMinSize(minHeight = 44.dp).padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Icon(PharmIcons.ReturnArrow, contentDescription = "ย้อนกลับ", tint = t.colors.fg3, modifier = Modifier.size(16.dp))
                Text("กลับ", style = PharmText.body.copy(color = t.colors.fg3))
            }
            Text("/", style = PharmText.body.copy(color = t.colors.fgMuted))
            Text(state.po?.docNo ?: "ใบรับสินค้า", style = PharmText.h1, modifier = Modifier.weight(1f))
            state.po?.let { po ->
                Row(
                    modifier = Modifier.clip(t.shapes.sm).clickable(role = Role.Button, onClick = { callbacks.onEdit(po.id) }).defaultMinSize(minWidth = 44.dp, minHeight = 44.dp).padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Icon(PharmIcons.Pencil, contentDescription = "แก้ไข", tint = t.colors.fg2, modifier = Modifier.size(20.dp))
                }
            }
        }

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            when {
                state.loading && state.po == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress()
                }
                state.po == null -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("ไม่พบใบรับสินค้า", style = PharmText.body.copy(color = t.colors.fg2))
                }
                else -> Body(po = state.po)
            }
        }

        state.po?.let { po -> ActionBar(po = po, state = state, callbacks = callbacks) }
    }

    if (state.confirmDialog) {
        PharmModal(
            open = true,
            onDismiss = callbacks.onCancelConfirm,
            title = "ยืนยันรับสินค้า?",
            footer = {
                PharmButton(
                    label = "ยกเลิก",
                    onClick = callbacks.onCancelConfirm,
                    variant = PharmButtonVariant.Ghost,
                    enabled = !state.confirming,
                )
                PharmButton(
                    label = "ยืนยัน",
                    onClick = callbacks.onConfirmNow,
                    enabled = !state.confirming,
                    loading = state.confirming,
                )
            },
        ) {
            Text(
                "เมื่อยืนยันแล้วระบบจะเพิ่มล็อต + อัปเดตสต็อก + บันทึก ขย.9 — ไม่สามารถยกเลิกได้",
                style = PharmText.body,
            )
        }
    }
    if (state.deleteDialog) {
        PharmModal(
            open = true,
            onDismiss = callbacks.onCancelDelete,
            title = "ลบใบรับสินค้า?",
            footer = {
                PharmButton(
                    label = "ยกเลิก",
                    onClick = callbacks.onCancelDelete,
                    variant = PharmButtonVariant.Ghost,
                    enabled = !state.deleting,
                )
                PharmButton(
                    label = "ลบ",
                    onClick = callbacks.onDeleteNow,
                    variant = PharmButtonVariant.Danger,
                    enabled = !state.deleting,
                    loading = state.deleting,
                )
            },
        ) {
            Text(
                "ใบนี้ยังไม่ได้ยืนยัน — ลบแล้วจะไม่สามารถกู้คืนได้ " +
                    "(สต็อกและล็อตยังไม่ถูกแตะต้อง)",
                style = PharmText.body,
            )
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun Body(po: PurchaseOrder) {
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item("header") { HeaderBlock(po) }
        item("section") {
            Text(
                text = "รายการสินค้า · ${po.itemCount} รายการ",
                style = PharmText.h3,
                modifier = Modifier.padding(top = 12.dp, bottom = 4.dp),
            )
        }
        items(po.items) { item -> ItemRow(item) }
    }
}

@Composable
private fun HeaderBlock(po: PurchaseOrder) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.surface, t.shapes.md)
            .border(1.dp, t.colors.borderSubtle, t.shapes.md)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StatusChip(po.status)
            Text(
                text = "รวม ${formatBahtCurrency(po.totalCost)}",
                style = PharmText.h2.tabular(),
            )
        }
        Box(Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
        DetailRow("ผู้จัดจำหน่าย", po.supplier.ifBlank { "-" })
        DetailRow("เลขที่ Invoice", po.invoiceNo.ifBlank { "-" })
        DetailRow("วันที่รับ", po.receiveDate.take(10).ifBlank { "-" })
        if (po.notes.isNotBlank()) DetailRow("หมายเหตุ", po.notes)
        DetailRow("สร้างเมื่อ", po.createdAt.take(19).replace('T', ' '))
        po.confirmedAt?.let {
            DetailRow("ยืนยันเมื่อ", it.take(19).replace('T', ' '))
        }
    }
}

@Composable
private fun StatusChip(status: PurchaseOrderStatus) {
    val pharmStatus = when (status) {
        PurchaseOrderStatus.Draft     -> PharmStatus.Draft
        PurchaseOrderStatus.Confirmed -> PharmStatus.Confirmed
    }
    PharmStatusBadge(status = pharmStatus)
}

@Composable
private fun DetailRow(label: String, value: String) {
    val t = pharmTokens
    Row(verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            style = PharmText.h3.copy(color = t.colors.fg2),
            modifier = Modifier.padding(end = 12.dp),
        )
        Text(
            text = value,
            style = PharmText.body,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun ItemRow(item: PurchaseOrderItem) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.md)
            .background(t.colors.surface, t.shapes.md)
            .border(1.dp, t.colors.borderSubtle, t.shapes.md)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.drugName.ifBlank { "(ไม่ระบุยา)" },
                style = PharmText.body,
            )
            Text(
                text = "ล็อต ${item.lotNumber} · หมดอายุ ${item.expiryDate}",
                style = PharmText.bodySm.tabular().copy(color = t.colors.fg2),
            )
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "${item.qty} ชิ้น",
                style = PharmText.h3.tabular(),
            )
            Text(
                text = "@${formatBaht(item.costPrice)}",
                style = PharmText.bodySm.tabular().copy(color = t.colors.fg2),
            )
        }
    }
}

@Composable
private fun ActionBar(
    po: PurchaseOrder,
    state: ImportDetailUiState,
    callbacks: ImportDetailCallbacks,
) {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxWidth().background(t.colors.surface)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (po.status == PurchaseOrderStatus.Draft) {
                PharmButton(
                    label = "ลบ",
                    onClick = callbacks.onAskDelete,
                    variant = PharmButtonVariant.Outline,
                    enabled = !state.confirming && !state.deleting,
                    leadingIcon = {
                        Icon(PharmIcons.Trash, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                )
                PharmButton(
                    label = "ยืนยันรับสินค้า",
                    onClick = callbacks.onAskConfirm,
                    modifier = Modifier.weight(1f),
                    enabled = !state.confirming && !state.deleting && po.itemCount > 0,
                    loading = state.confirming,
                    leadingIcon = {
                        Icon(PharmIcons.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                    },
                )
            } else {
                Text(
                    text = "ยืนยันแล้ว — ใบนี้ถูกบันทึกในสต็อกเรียบร้อย",
                    style = PharmText.h3.copy(color = t.colors.successFg),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(t.shapes.md)
                        .background(t.colors.successBg, t.shapes.md)
                        .padding(horizontal = 12.dp, vertical = 12.dp),
                )
            }
        }
    }
}

private val previewItems = listOf(
    PurchaseOrderItem(
        drugId = "d1",
        drugName = "พาราเซตามอล 500mg",
        lotNumber = "A12345",
        expiryDate = "2027-06-30",
        qty = 100,
        costPrice = 1.25,
        sellPrice = 2.0,
    ),
    PurchaseOrderItem(
        drugId = "d2",
        drugName = "อะม็อกซีซิลลิน 250mg",
        lotNumber = "B67890",
        expiryDate = "2026-12-31",
        qty = 50,
        costPrice = 3.5,
        sellPrice = null,
    ),
)

private fun previewPo(status: PurchaseOrderStatus) = PurchaseOrder(
    id = "po-1",
    docNo = "GR-2026-0001",
    supplier = "บริษัท เอ บี ซี ฟาร์มา",
    invoiceNo = "INV-001",
    receiveDate = "2026-06-05",
    items = previewItems,
    itemCount = previewItems.size,
    totalCost = 300.0,
    status = status,
    notes = "รับของครบ",
    createdAt = "2026-06-05T09:30:00",
    confirmedAt = if (status == PurchaseOrderStatus.Confirmed) "2026-06-05T10:00:00" else null,
)

@Preview
@Composable
private fun ImportDetailContent_Loaded_Preview() {
    PharmacyTheme {
        ImportDetailContent(
            state = ImportDetailUiState(po = previewPo(PurchaseOrderStatus.Draft)),
            callbacks = ImportDetailCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ImportDetailContent_Empty_Preview() {
    PharmacyTheme {
        ImportDetailContent(
            state = ImportDetailUiState(po = null),
            callbacks = ImportDetailCallbacks(),
        )
    }
}

@Preview
@Composable
private fun ImportDetailContent_Confirmed_Preview() {
    PharmacyTheme {
        ImportDetailContent(
            state = ImportDetailUiState(po = previewPo(PurchaseOrderStatus.Confirmed)),
            callbacks = ImportDetailCallbacks(),
        )
    }
}
