package app.devper.pharm.presentation.stock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.devper.pharm.domain.model.Drug
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun StockScreen(
    onAddDrug: () -> Unit = {},
    onEditDrug: (drugId: String) -> Unit = {},
    onOpenLots: (drugId: String) -> Unit = {},
    onOpenAdjust: (drugId: String) -> Unit = {},
    onOpenHistory: (drugId: String) -> Unit = {},
    onOpenReorderSuggestions: () -> Unit = {},
    viewModel: StockViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
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
            onOpenHistory = { onOpenHistory(byId(it)) },
            onOpenReorderSuggestions = onOpenReorderSuggestions,
            onDismissError = viewModel::dismissError,
        ),
    )
}
