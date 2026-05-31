package app.devper.pharm.presentation.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsEditorViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    SettingsContent(
        state = state,
        editor = SettingsEditorCallbacks(
            onSelectTab = viewModel::selectTab,
            onStoreName = viewModel::onStoreName,
            onStoreAddress = viewModel::onStoreAddress,
            onStorePhone = viewModel::onStorePhone,
            onStoreTaxId = viewModel::onStoreTaxId,
            onReceiptHeader = viewModel::onReceiptHeader,
            onReceiptFooter = viewModel::onReceiptFooter,
            onReceiptPaperWidth = viewModel::onReceiptPaperWidth,
            onReceiptShowPharmacist = viewModel::onReceiptShowPharmacist,
            onStockLowThreshold = viewModel::onStockLowThreshold,
            onStockReorderDays = viewModel::onStockReorderDays,
            onStockReorderLookahead = viewModel::onStockReorderLookahead,
            onStockExpiringDays = viewModel::onStockExpiringDays,
            onPharmacistName = viewModel::onPharmacistName,
            onPharmacistLicenseNo = viewModel::onPharmacistLicenseNo,
            onKySkipAuto = viewModel::onKySkipAuto,
            onKyDefaultBuyerAddress = viewModel::onKyDefaultBuyerAddress,
            onTimezone = viewModel::onTimezone,
            onSubmit = viewModel::submit,
            onDismissMessage = viewModel::dismissMessage,
            onDismissError = viewModel::dismissError,
        ),
    )
}
