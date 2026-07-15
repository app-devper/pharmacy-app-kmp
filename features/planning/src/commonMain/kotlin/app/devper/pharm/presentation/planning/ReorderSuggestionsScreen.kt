package app.devper.pharm.presentation.planning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.common.ReloadOnResume
import app.devper.pharm.presentation.planning.i18n.localizePlanningMessage
import app.devper.pharm.ui.i18n.pharmStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ReorderSuggestionsScreen(
    onBack: () -> Unit,
    onOpenPurchaseOrder: () -> Unit = {},
    viewModel: ReorderSuggestionsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(viewModel::reload)

    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings
    LaunchedEffect(state.messageState) {
        state.messageState?.let {
            snackbar.showToast(PharmToast.Success(it.localizePlanningMessage(s)))
            viewModel.dismissMessage()
        }
    }

    ReorderSuggestionsContent(
        state = state,
        onBack = onBack,
        callbacks = ReorderSuggestionsCallbacks(
            onReload = viewModel::reload,
            onAddToPurchaseOrder = viewModel::addToPurchaseOrder,
            onAddAll = viewModel::addAllToPurchaseOrder,
            onOpenPurchaseOrder = onOpenPurchaseOrder,
            onDismiss = viewModel::dismissSuggestion,
            onDismissError = viewModel::dismissError,
        ),
    )
}
