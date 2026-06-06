package app.devper.pharm.presentation.suppliers

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun SuppliersListContent(
    state: SuppliersListUiState,
    callbacks: SuppliersListCallbacks = SuppliersListCallbacks(),
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
            SuppliersListToolbar(query = state.query, callbacks = callbacks)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            PharmListResultLine(total = state.suppliers.size, noun = "ราย", visible = visible.size, searching = searching)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.suppliers.isEmpty() -> PharmListSkeleton()
                else -> SuppliersListTable(
                    suppliers = visible,
                    callbacks = callbacks,
                    emptySearching = searching,
                )
            }
        }
    }

    state.pendingDelete?.let { pending ->
        DeleteSupplierModal(
            supplier = pending,
            deleting = state.deleting,
            onConfirm = callbacks.onConfirmDelete,
            onDismiss = callbacks.onCancelDelete,
        )
    }

    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun DeleteSupplierModal(
    supplier: Supplier,
    deleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = "ลบซัพพลายเออร์?",
        subtitle = supplier.name,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = "ยกเลิก",
                onClick = onDismiss,
                variant = PharmButtonVariant.Outline,
                enabled = !deleting,
            )
            PharmButton(
                label = "ลบ",
                onClick = onConfirm,
                variant = PharmButtonVariant.Danger,
                enabled = !deleting,
            )
        },
    ) {
        Text(
            text = "ต้องการลบ \"${supplier.name}\" ออกจากระบบหรือไม่ — ใบรับสินค้าเดิมจะยังคงเก็บชื่อนี้ไว้",
            style = PharmText.body,
        )
    }
}

private val sampleSuppliers = listOf(
    Supplier(
        id = "1",
        name = "บ. ฟาร์มาแกรนด์ จำกัด",
        contactName = "คุณนิภาพร โทร",
        phone = "02-123-4567",
        address = "",
        taxId = "0105557123456",
        notes = "ส่งฟรีเมื่อสั่ง > ฿10,000",
    ),
    Supplier(
        id = "2",
        name = "บ. ไทยฟาร์มา จำกัด",
        contactName = "คุณวีระชัย",
        phone = "02-555-1234",
        address = "",
        taxId = "0105545987654",
        notes = "",
    ),
    Supplier(
        id = "3",
        name = "หจก. สมุนไพรไทย",
        contactName = "คุณนงนุช",
        phone = "034-888-9999",
        address = "",
        taxId = "0343556222111",
        notes = "รับเฉพาะเงินสด",
    ),
    Supplier(
        id = "4",
        name = "บ. เฮลท์ตี้ดิสตริบิวเตอร์",
        contactName = "คุณภาสกร",
        phone = "02-777-2233",
        address = "",
        taxId = "0105560778899",
        notes = "ส่งทุกวันพุธ",
    ),
)

@Preview
@Composable
private fun SuppliersListContent_Loaded_Preview() {
    PharmacyTheme {
        SuppliersListContent(state = SuppliersListUiState(suppliers = sampleSuppliers))
    }
}

@Preview
@Composable
private fun SuppliersListContent_Empty_Preview() {
    PharmacyTheme {
        SuppliersListContent(state = SuppliersListUiState(suppliers = emptyList()))
    }
}

@Preview
@Composable
private fun SuppliersListContent_Loading_Preview() {
    PharmacyTheme {
        SuppliersListContent(state = SuppliersListUiState(loading = true))
    }
}
