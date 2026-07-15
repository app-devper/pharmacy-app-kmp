package app.devper.pharm.presentation.labels

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.common.ReloadOnResume
import app.devper.pharm.ui.i18n.localize
import app.devper.pharm.ui.i18n.pharmStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LabelPrintScreen(viewModel: LabelPrintViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings

    ReloadOnResume(viewModel::reload)

    LaunchedEffect(state.messageState) {
        state.messageState?.let {
            snackbar.showToast(PharmToast.Info(it.localize(s)))
            viewModel.dismissMessage()
        }
    }

    LabelPrintContent(
        state = state,
        callbacks = LabelPrintCallbacks(
            onQueryChange = viewModel::onQueryChange,
            onAddDrug = viewModel::onAddDrug,
            onRemoveLine = viewModel::onRemoveLine,
            onChangeCopies = viewModel::onChangeCopies,
            onChangeBarcode = viewModel::onChangeBarcode,
            onToggleIncludePrice = viewModel::onToggleIncludePrice,
            onSizeChange = viewModel::onSizeChange,
            onAskClearAll = viewModel::onAskClearAll,
            onCancelClearAll = viewModel::onCancelClearAll,
            onConfirmClearAll = viewModel::onConfirmClearAll,
            onPrint = viewModel::onPrint,
            onDismissError = viewModel::dismissError,
        ),
    )
}
