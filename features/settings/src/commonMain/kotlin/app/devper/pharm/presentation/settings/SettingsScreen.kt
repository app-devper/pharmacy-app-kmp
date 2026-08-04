package app.devper.pharm.presentation.settings

import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import app.devper.pharm.ui.common.ReloadOnResume
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SettingsScreen(
    viewModel: SettingsEditorViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(viewModel::reload)

    SettingsContent(
        state = state,
        editor = viewModel.settingsEditorCallbacks(),
    )
}

internal fun SettingsEditorViewModel.settingsEditorCallbacks() = SettingsEditorCallbacks(
    onSelectTab = ::selectTab,
    onThemeChange = ::onThemeChange,
    onFontSizeChange = ::onFontSizeChange,
    onDensityChange = ::onDensityChange,
    onLocaleChange = ::onLocaleChange,
    onStoreName = ::onStoreName,
    onStoreAddress = ::onStoreAddress,
    onStorePhone = ::onStorePhone,
    onStoreTaxId = ::onStoreTaxId,
    onReceiptHeader = ::onReceiptHeader,
    onReceiptFooter = ::onReceiptFooter,
    onReceiptPaperWidth = ::onReceiptPaperWidth,
    onReceiptShowPharmacist = ::onReceiptShowPharmacist,
    onStockLowThreshold = ::onStockLowThreshold,
    onStockReorderDays = ::onStockReorderDays,
    onStockReorderLookahead = ::onStockReorderLookahead,
    onStockExpiringDays = ::onStockExpiringDays,
    onPharmacistName = ::onPharmacistName,
    onPharmacistLicenseNo = ::onPharmacistLicenseNo,
    onKySkipAuto = ::onKySkipAuto,
    onConfirmKySkip = ::confirmKySkipAuto,
    onCancelKySkip = ::cancelKySkipAuto,
    onTestPrint = ::testPrint,
    onKyDefaultBuyerAddress = ::onKyDefaultBuyerAddress,
    onTimezone = ::onTimezone,
    onSubmit = ::submit,
    onDismissMessage = ::dismissMessage,
    onDismissError = ::dismissError,
)
