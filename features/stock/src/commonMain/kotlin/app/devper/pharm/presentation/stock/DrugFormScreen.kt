package app.devper.pharm.presentation.stock

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.presentation.stock.form.DrugFormCallbacks
import app.devper.pharm.presentation.stock.form.DrugFormContent
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmToast
import app.devper.pharm.ui.components.RegisterUnsavedChanges
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun DrugFormScreen(
    drugId: String?,
    onBack: () -> Unit,
    viewModel: DrugFormViewModel = koinViewModel(),
    lotsViewModel: DrugLotsViewModel = koinViewModel(),
    adjustmentsViewModel: StockAdjustmentsViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    RegisterUnsavedChanges(state.hasUnsavedChanges)
    val lotsState by lotsViewModel.state.collectAsStateWithLifecycle()
    val adjustmentsState by adjustmentsViewModel.state.collectAsStateWithLifecycle()
    val snackbar = LocalPharmSnackbar.current

    LaunchedEffect(drugId) {
        viewModel.init(if (drugId.isNullOrBlank()) DrugFormMode.Add else DrugFormMode.Edit(drugId))
    }
    val s = pharmStrings
    LaunchedEffect(state.saved) {
        if (state.saved) {
            viewModel.resetSaved()
            snackbar.showToast(PharmToast.Success(s.commonSaved))
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

    DrugLotsBottomSheet(
        state = lotsState,
        callbacks = DrugLotsCallbacks(
            onClose = lotsViewModel::close,
            onRequestDelete = lotsViewModel::requestDelete,
            onCancelDelete = lotsViewModel::cancelDelete,
            onConfirmDelete = lotsViewModel::confirmDelete,
            onToggleAddForm = lotsViewModel::toggleAddForm,
            onLotNumber = lotsViewModel::onLotNumber,
            onExpiryDate = lotsViewModel::onExpiryDate,
            onQuantity = lotsViewModel::onQuantity,
            onCostPrice = lotsViewModel::onCostPrice,
            onSellPrice = lotsViewModel::onSellPrice,
            onSubmitAdd = lotsViewModel::submitAdd,
            onDismissError = lotsViewModel::dismissError,
        ),
        onDismiss = adjustmentsViewModel::reload,
    )
    StockAdjustmentsBottomSheet(
        state = adjustmentsState,
        callbacks = StockAdjustmentsCallbacks(
            onClose = adjustmentsViewModel::close,
            onToggleAddForm = adjustmentsViewModel::toggleAddForm,
            onSign = adjustmentsViewModel::onSign,
            onAbsDelta = adjustmentsViewModel::onAbsDelta,
            onReason = adjustmentsViewModel::onReason,
            onNote = adjustmentsViewModel::onNote,
            onSubmitAdd = adjustmentsViewModel::submitAdd,
            onDismissError = adjustmentsViewModel::dismissError,
        ),
        onDismiss = lotsViewModel::reload,
    )
}
