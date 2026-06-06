package app.devper.pharm.presentation.stock

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.domain.model.Drug
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StockScreen(
    onAddDrug: () -> Unit = {},
    onEditDrug: (drugId: String) -> Unit = {},
    onOpenLots: (drugId: String) -> Unit = {},
    onOpenAdjust: (drugId: String) -> Unit = {},
    onOpenHistory: (drugId: String, drugName: String) -> Unit = { _, _ -> },
    onOpenReorderSuggestions: () -> Unit = {},
    viewModel: StockViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val byId: (Drug) -> String = { it.id }

    StockContent(
        state = state,
        callbacks = StockCallbacks(
            onQueryChange = viewModel::onQueryChange,
            onTypeFilterChange = viewModel::onTypeFilterChange,
            onAddDrug = onAddDrug,
            onEditDrug = { onEditDrug(byId(it)) },
            onOpenLots = { onOpenLots(byId(it)) },
            onOpenAdjust = { onOpenAdjust(byId(it)) },
            onOpenHistory = { onOpenHistory(it.id, it.name) },
            onOpenReorderSuggestions = onOpenReorderSuggestions,
            onDismissError = viewModel::dismissError,
        ),
    )
}
