package app.devper.pharm.presentation.stock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.toToast
import app.devper.pharm.ui.common.ReloadOnResume
import app.devper.pharm.ui.i18n.localize
import app.devper.pharm.ui.i18n.pharmStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StockScreen(
    onAddDrug: () -> Unit = {},
    onEditDrug: (drugId: String) -> Unit = {},
    onOpenLots: (drugId: String, drugName: String) -> Unit = { _, _ -> },
    onOpenAdjust: (drugId: String, drugName: String) -> Unit = { _, _ -> },
    onOpenHistory: (drugId: String, drugName: String) -> Unit = { _, _ -> },
    onOpenReorderSuggestions: () -> Unit = {},
    onOpenExpiry: () -> Unit = {},
    onOpenImports: () -> Unit = {},
    viewModel: StockViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val byId: (Drug) -> String = { it.id }

    ReloadOnResume(viewModel::reload)

    val snackbar = LocalPharmSnackbar.current
    val s = pharmStrings
    LaunchedEffect(state.messageState) {
        state.messageState?.let {
            snackbar.showToast(it.toToast(s))
            viewModel.dismissMessage()
        }
    }

    StockContent(
        state = state,
        callbacks = StockCallbacks(
            onQueryChange = viewModel::onQueryChange,
            onTypeFilterChange = viewModel::onTypeFilterChange,
            onAddDrug = onAddDrug,
            onEditDrug = { onEditDrug(byId(it)) },
            onOpenLots = { onOpenLots(it.id, it.name) },
            onOpenAdjust = { onOpenAdjust(it.id, it.name) },
            onOpenHistory = { onOpenHistory(it.id, it.name) },
            onExportExcel = viewModel::onExportExcel,
            onImport = onOpenImports,
            onOpenReorderSuggestions = onOpenReorderSuggestions,
            onOpenExpiry = onOpenExpiry,
            onDismissError = viewModel::dismissError,
        ),
    )
}
