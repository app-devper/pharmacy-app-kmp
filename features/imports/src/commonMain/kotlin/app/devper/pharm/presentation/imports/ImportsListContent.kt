package app.devper.pharm.presentation.imports

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity

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
import app.devper.pharm.domain.model.PurchaseOrderStatus
import app.devper.pharm.domain.model.PurchaseOrderSummary
import app.devper.pharm.presentation.imports.i18n.localizeImports
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ImportsListContent(
    state: ImportsListUiState,
    callbacks: ImportsListCallbacks = ImportsListCallbacks(),
) {
    val visible = state.filtered
    val searching = state.query.isNotBlank()

    PharmListScaffold(
        toolbar = {
            ImportsListToolbar(
                query = state.query,
                draftCount = state.draftCount,
                callbacks = callbacks,
            )
        },
        resultLine = {
            PharmListResultLine(total = state.orders.size, noun = pharmStrings.importsCountNoun, visible = visible.size, searching = searching)
        },
    ) {
        when {
            state.loading && state.orders.isEmpty() -> PharmListSkeleton()
            else -> ImportsListTable(
                orders = visible,
                callbacks = callbacks,
                emptySearching = searching,
            )
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

    ErrorBottomSheet(message = state.errorState?.localizeImports(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
private fun ConfirmImportModal(
    order: PurchaseOrderSummary,
    busy: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    val s = pharmStrings
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = s.importsConfirmReceiveTitle,
        subtitle = order.docNo,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = onDismiss,
                variant = PharmButtonVariant.Outline,
                enabled = !busy,
            )
            PharmButton(
                label = s.importsActionConfirmReceive,
                onClick = onConfirm,
                variant = PharmButtonVariant.Primary,
                enabled = !busy,
            )
        },
    ) {
        Text(
            text = s.importsConfirmReceiveSubtitle,
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
    val s = pharmStrings
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = s.importsConfirmDeleteDraftTitle,
        subtitle = order.docNo,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = s.commonCancel,
                onClick = onDismiss,
                variant = PharmButtonVariant.Outline,
                enabled = !busy,
            )
            PharmButton(
                label = s.commonDelete,
                onClick = onConfirm,
                variant = PharmButtonVariant.Danger,
                enabled = !busy,
            )
        },
    ) {
        Text(
            text = s.importsConfirmDeleteReceivedMessage,
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
        receiveDate = kotlinx.datetime.LocalDate.parse("2026-05-16"),
        itemCount = 8,
        totalCost = Money(32400.0),
        status = PurchaseOrderStatus.Confirmed,
        notes = "",
        createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-16T09:00:00"),
        confirmedAt = kotlinx.datetime.LocalDateTime.parse("2026-05-16T10:00:00"),
    ),
    PurchaseOrderSummary(
        id = "2",
        docNo = "IMP-260516-001",
        supplier = "หจก. สมุนไพรไทย",
        invoiceNo = "INV-26050-072",
        receiveDate = kotlinx.datetime.LocalDate.parse("2026-05-16"),
        itemCount = 3,
        totalCost = Money(5400.0),
        status = PurchaseOrderStatus.Confirmed,
        notes = "",
        createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-16T11:00:00"),
        confirmedAt = kotlinx.datetime.LocalDateTime.parse("2026-05-16T12:00:00"),
    ),
    PurchaseOrderSummary(
        id = "3",
        docNo = "IMP-260514-001",
        supplier = "บ. ไทยฟาร์มา จำกัด",
        invoiceNo = "INV-26050-027",
        receiveDate = kotlinx.datetime.LocalDate.parse("2026-05-14"),
        itemCount = 5,
        totalCost = Money(19800.0),
        status = PurchaseOrderStatus.Confirmed,
        notes = "",
        createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-14T09:00:00"),
        confirmedAt = kotlinx.datetime.LocalDateTime.parse("2026-05-14T10:00:00"),
    ),
    PurchaseOrderSummary(
        id = "4",
        docNo = "IMP-260513-001",
        supplier = "บ. เฮลท์ตี้ดิสตริบิวเตอร์",
        invoiceNo = "INV-26050-041",
        receiveDate = kotlinx.datetime.LocalDate.parse("2026-05-13"),
        itemCount = 12,
        totalCost = Money(48700.0),
        status = PurchaseOrderStatus.Draft,
        notes = "",
        createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-13T09:00:00"),
        confirmedAt = null,
    ),
    PurchaseOrderSummary(
        id = "5",
        docNo = "IMP-260510-002",
        supplier = "บ. ฟาร์มาแกรนด์ จำกัด",
        invoiceNo = "INV-26050-008",
        receiveDate = kotlinx.datetime.LocalDate.parse("2026-05-10"),
        itemCount = 4,
        totalCost = Money(9360.0),
        status = PurchaseOrderStatus.Confirmed,
        notes = "",
        createdAt = kotlinx.datetime.LocalDateTime.parse("2026-05-10T09:00:00"),
        confirmedAt = kotlinx.datetime.LocalDateTime.parse("2026-05-10T10:00:00"),
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
