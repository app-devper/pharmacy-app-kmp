package app.devper.pharm.presentation.saleshistory

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.domain.model.SaleSummary
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SalesHistoryScreen(
    onOpenReceipt: (saleId: String) -> Unit = {},
    viewModel: SalesHistoryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val byId: (SaleSummary) -> String = { it.id }

    SalesHistoryContent(
        state = state,
        callbacks = SalesHistoryCallbacks(
            onQueryChange = viewModel::onQueryChange,
            onFromMillisChange = viewModel::onFromMillisChange,
            onToMillisChange = viewModel::onToMillisChange,
            onApplyFilter = viewModel::applyFilter,
            onOpenReceipt = { sale ->
                viewModel.onSelectSale(sale)
                onOpenReceipt(byId(sale))
            },
            onStartReturn = viewModel::onStartReturn,
            onDismissError = viewModel::dismissError,
        ),
    )

    if (state.returnSheetOpen) {
        state.selected?.let { sale ->
            ReturnSaleSheet(
                sale = sale,
                items = state.items,
                draft = state.returnDraft,
                reason = state.returnReason,
                submitting = state.submittingReturn,
                onLineQtyChange = viewModel::onReturnLineQtyChange,
                onReasonChange = viewModel::onReturnReasonChange,
                onConfirm = viewModel::confirmReturn,
                onDismiss = viewModel::onCloseReturnSheet,
            )
        }
    }
}
