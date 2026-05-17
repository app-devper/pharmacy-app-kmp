package app.devper.pharm.presentation.stock

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.devper.pharm.presentation.stock.form.DrugFormCallbacks
import app.devper.pharm.presentation.stock.form.DrugFormContent
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DrugFormScreen(
    drugId: String?,
    onBack: () -> Unit,
    viewModel: DrugFormViewModel = koinViewModel(),
    lotsViewModel: DrugLotsViewModel = koinViewModel(),
    adjustmentsViewModel: StockAdjustmentsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(drugId) {
        viewModel.init(if (drugId.isNullOrBlank()) DrugFormMode.Add else DrugFormMode.Edit(drugId))
    }
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            onBack()
        }
    }

    DrugFormContent(
        state = state,
        callbacks = DrugFormCallbacks(
            onSubmit = viewModel::submit,
            onBack = onBack,
            onDismissError = viewModel::dismissError,
            onOpenLots = lotsViewModel::open,
            onOpenAdjustments = adjustmentsViewModel::open,
            onName = viewModel::onName,
            onGenericName = viewModel::onGenericName,
            onType = viewModel::onType,
            onStrength = viewModel::onStrength,
            onUnit = viewModel::onUnit,
            onBarcode = viewModel::onBarcode,
            onRegNo = viewModel::onRegNo,
            onSellPrice = viewModel::onSellPrice,
            onCostPrice = viewModel::onCostPrice,
            onTierRetail = viewModel::onTierRetail,
            onTierRegular = viewModel::onTierRegular,
            onTierWholesale = viewModel::onTierWholesale,
            onMinStock = viewModel::onMinStock,
            onAddAltUnit = viewModel::onAddAltUnit,
            onRemoveAltUnit = viewModel::onRemoveAltUnit,
            onAltUnitName = viewModel::onAltUnitName,
            onAltUnitFactor = viewModel::onAltUnitFactor,
            onAltUnitSellPrice = viewModel::onAltUnitSellPrice,
            onAltUnitBarcode = viewModel::onAltUnitBarcode,
            onAltUnitHidden = viewModel::onAltUnitHidden,
            onToggleReportType = viewModel::onToggleReportType,
            onInitialStock = viewModel::onInitialStock,
            onLotNumber = viewModel::onLotNumber,
            onLotExpiry = viewModel::onLotExpiry,
            onLotQty = viewModel::onLotQty,
            onLotCostPrice = viewModel::onLotCostPrice,
            onLotSellPrice = viewModel::onLotSellPrice,
        ),
    )

    DrugLotsBottomSheet(viewModel = lotsViewModel, onDismiss = {  })
    StockAdjustmentsBottomSheet(viewModel = adjustmentsViewModel, onDismiss = {  })
}
