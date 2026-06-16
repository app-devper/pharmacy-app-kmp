package app.devper.pharm.presentation.saleshistory

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SalesHistoryScreen(
    viewModel: SalesHistoryViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    ReloadOnResume(viewModel::loadList)

    SalesHistoryContent(
        state = state,
        callbacks = SalesHistoryCallbacks(
            onQueryChange = viewModel::onQueryChange,
            onFromMillisChange = viewModel::onFromMillisChange,
            onToMillisChange = viewModel::onToMillisChange,
            onApplyFilter = viewModel::applyFilter,
            onSelectRange = viewModel::onSelectRange,
            onOpenReceipt = viewModel::onViewBill,
            onStartReturn = viewModel::onStartReturn,
            onDismissError = viewModel::dismissError,
        ),
    )

    if (state.billSheetOpen) {
        state.selected?.let { sale ->
            BillDetailSheet(
                sale = sale,
                items = state.items,
                itemsLoading = state.itemsLoading,
                onDismiss = viewModel::onCloseBill,
            )
        }
    }

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
