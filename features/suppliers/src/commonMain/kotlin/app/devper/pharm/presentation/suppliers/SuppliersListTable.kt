package app.devper.pharm.presentation.suppliers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SuppliersListTable(
    suppliers: List<Supplier>,
    callbacks: SuppliersListCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val t = pharmTokens
    val columns = remember(callbacks, t) {
        listOf(
        PharmTableColumn<Supplier>(
            header = "ชื่อบริษัท / ร้านค้า",
            weight = 2.2f,
            cell = { supplier ->
                Text(
                    text = supplier.name,
                    style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = "ผู้ติดต่อ",
            weight = 1.4f,
            cell = { supplier ->
                Text(
                    text = supplier.contactName.ifBlank { "—" },
                    style = PharmText.bodySm.copy(
                        color = if (supplier.contactName.isBlank()) t.colors.fgMuted else t.colors.fg2,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = "เบอร์โทร",
            weight = 1.2f,
            cell = { supplier ->
                Text(
                    text = supplier.phone.ifBlank { "—" },
                    style = PharmText.meta.copy(
                        color = if (supplier.phone.isBlank()) t.colors.fgMuted else t.colors.fg3,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = "เลขผู้เสียภาษี",
            weight = 1.3f,
            cell = { supplier ->
                Text(
                    text = supplier.taxId.ifBlank { "—" },
                    style = PharmText.micro.copy(
                        color = if (supplier.taxId.isBlank()) t.colors.fgMuted else t.colors.fg3,
                        fontFamily = FontFamily.Monospace,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = "หมายเหตุ",
            weight = 1.6f,
            cell = { supplier ->
                Text(
                    text = supplier.notes.ifBlank { "—" },
                    style = PharmText.micro.copy(
                        color = if (supplier.notes.isBlank()) t.colors.fgMuted else t.colors.fg3,
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            },
        ),
        PharmTableColumn(
            header = "จัดการ",
            weight = 0.6f,
            cell = { supplier -> SupplierRowActions(supplier = supplier, callbacks = callbacks) },
        ),
        )
    }

    PharmTable(
        rows = suppliers,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { supplier -> callbacks.onOpenDetail(supplier) },
        rowHeight = 56.dp,
        emptyContent = {
            Text(
                text = if (emptySearching) "ไม่พบซัพพลายเออร์ตามที่ค้นหา" else "ยังไม่มีซัพพลายเออร์",
                style = PharmText.meta,
            )
        },
    )
}

@Composable
private fun SupplierRowActions(supplier: Supplier, callbacks: SuppliersListCallbacks) {
    PharmActionMenu(
        actions = listOf(
            PharmAction(
                label = "รายละเอียด",
                icon = PharmIcons.Suppliers,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onOpenDetail(supplier) },
            ),
            PharmAction(
                label = "แก้ไข",
                icon = PharmIcons.Pencil,
                onClick = { callbacks.onOpenEdit(supplier) },
            ),
            PharmAction(
                label = "ลบ",
                icon = PharmIcons.Trash,
                tone = PharmActionTone.Danger,
                onClick = { callbacks.onRequestDelete(supplier) },
            ),
        ),
    )
}
