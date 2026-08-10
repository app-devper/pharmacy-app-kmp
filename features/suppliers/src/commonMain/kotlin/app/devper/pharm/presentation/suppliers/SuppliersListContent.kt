package app.devper.pharm.presentation.suppliers

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import app.devper.pharm.domain.model.Supplier
import app.devper.pharm.presentation.suppliers.i18n.localizeSuppliersList
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.components.unlessPageShowsError
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmErrorState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme

@Composable
fun SuppliersListContent(
    state: SuppliersListUiState,
    callbacks: SuppliersListCallbacks = SuppliersListCallbacks(),
) {
    val pageIsEmpty = state.suppliers.isEmpty()
    val s = pharmStrings
    val visible = state.filtered
    val searching = state.query.isNotBlank()

    PharmListScaffold(
        toolbar = { SuppliersListToolbar(query = state.query, callbacks = callbacks) },
        resultLine = {
            PharmListResultLine(total = state.suppliers.size, noun = s.customersCountNoun, visible = visible.size, searching = searching)
        },
    ) {
        when {
            state.loading && pageIsEmpty -> PharmListSkeleton()
            state.errorState != null && pageIsEmpty ->
                PharmErrorState()
            pageIsEmpty -> PharmEmptyState(
                icon = PharmIcons.Suppliers,
                title = s.suppliersListEmpty,
                action = {
                    PharmButton(
                        label = s.suppliersAddCta,
                        onClick = callbacks.onOpenAdd,
                        size = PharmButtonSize.Sm,
                    )
                },
            )
            else -> SuppliersListTable(
                suppliers = visible,
                callbacks = callbacks,
                emptySearching = searching,
            )
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

    ErrorBottomSheet(message = state.errorState.unlessPageShowsError(pageIsEmpty)?.localizeSuppliersList(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
private fun DeleteSupplierModal(
    supplier: Supplier,
    deleting: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val s = pharmStrings
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = s.suppliersDeleteConfirmTitle,
        subtitle = supplier.name,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = onDismiss,
                variant = PharmButtonVariant.Outline,
                enabled = !deleting,
            )
            PharmButton(
                label = s.commonDelete,
                onClick = onConfirm,
                variant = PharmButtonVariant.Danger,
                enabled = !deleting,
            )
        },
    ) {
        Text(
            text = s.suppliersDeleteConfirmMessage(supplier.name),
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
