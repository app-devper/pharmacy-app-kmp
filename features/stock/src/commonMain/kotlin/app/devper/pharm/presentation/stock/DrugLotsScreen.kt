package app.devper.pharm.presentation.stock

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.i18n.pharmStrings
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DrugLotsScreen(
    drugId: String,
    drugName: String,
    onBack: () -> Unit,
    viewModel: DrugLotsViewModel = koinViewModel(),
) {
    val t = pharmTokens
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(drugId, drugName) { viewModel.open(drugId, drugName) }

    val callbacks = DrugLotsCallbacks(
        onClose = {},
        onRequestDelete = viewModel::requestDelete,
        onCancelDelete = viewModel::cancelDelete,
        onConfirmDelete = viewModel::confirmDelete,
        onToggleAddForm = viewModel::toggleAddForm,
        onLotNumber = viewModel::onLotNumber,
        onExpiryDate = viewModel::onExpiryDate,
        onQuantity = viewModel::onQuantity,
        onCostPrice = viewModel::onCostPrice,
        onSellPrice = viewModel::onSellPrice,
        onSubmitAdd = viewModel::submitAdd,
        onDismissError = viewModel::dismissError,
    )

    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(title = pharmStrings.stockActionLots, onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            DrugLotsContent(
                state = state,
                callbacks = callbacks,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
            )
        }
    }

    DrugLotsDialogs(state = state, callbacks = callbacks)
}
