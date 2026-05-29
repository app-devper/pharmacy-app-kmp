package app.devper.pharm.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmTab
import app.devper.pharm.ui.designsystem.PharmTabBar
import app.devper.pharm.ui.theme.PharmacyTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import app.devper.pharm.ui.designsystem.PharmCircularProgress

private val SETTINGS_TABS: List<PharmTab> =
    SettingsTab.entries.map { PharmTab(id = it.name, label = it.label) }

@Composable
fun SettingsContent(
    state: SettingsEditorUiState,
    editor: SettingsEditorCallbacks = SettingsEditorCallbacks(),
) {
    Column(modifier = Modifier.fillMaxSize()) {
        SettingsSaveBar(state = state, onSave = editor.onSubmit, onMessageDismiss = editor.onDismissMessage)

        PharmTabBar(
            tabs = SETTINGS_TABS,
            activeId = state.tab.name,
            onSelect = { id -> editor.onSelectTab(SettingsTab.valueOf(id)) },
        )

        Box(modifier = Modifier.fillMaxSize()) {
            if (state.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress()
                }
            } else {
                SettingsTabBody(state = state, editor = editor)
            }
        }
    }

    ErrorBottomSheet(message = state.error, onDismiss = editor.onDismissError)
}

@Composable
private fun SettingsTabBody(state: SettingsEditorUiState, editor: SettingsEditorCallbacks) {
    val scroll = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scroll)
            .padding(horizontal = 16.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        when (state.tab) {
            SettingsTab.Store      -> SettingsStoreTab(state, editor)
            SettingsTab.Receipt    -> SettingsReceiptTab(state, editor)
            SettingsTab.Stock      -> SettingsStockTab(state, editor)
            SettingsTab.Pharmacist -> SettingsPharmacistTab(state, editor)
            SettingsTab.Ky         -> SettingsKyTab(state, editor)
        }
    }
}

@Preview
@Composable
private fun SettingsContent_Store_Preview() {
    PharmacyTheme {
        SettingsContent(
            state = SettingsEditorUiState(
                form = SettingsFormFields(
                    storeName = "ร้านยาเด่นเภสัช",
                    storeAddress = "12/3 ถ.พหลโยธิน เขตจตุจักร กทม.",
                    storePhone = "021234567",
                ),
            ),
            editor = SettingsEditorCallbacks(),
        )
    }
}

@Preview
@Composable
private fun SettingsContent_Receipt_Preview() {
    PharmacyTheme {
        SettingsContent(
            state = SettingsEditorUiState(
                tab = SettingsTab.Receipt,
                form = SettingsFormFields(
                    storeName = "ร้านยา",
                    receiptHeader = "ใบเสร็จ/ใบกำกับภาษีอย่างย่อ",
                    receiptFooter = "ขอบคุณที่ใช้บริการ",
                    receiptShowPharmacist = true,
                ),
            ),
        )
    }
}

@Preview
@Composable
private fun SettingsContent_Loading_Preview() {
    PharmacyTheme {
        SettingsContent(
            state = SettingsEditorUiState(loading = true),
        )
    }
}

@Preview
@Composable
private fun SettingsContent_Dirty_Preview() {
    PharmacyTheme {
        SettingsContent(
            state = SettingsEditorUiState(
                form = SettingsFormFields(storeName = "ร้านยา (เปลี่ยนชื่อแล้ว)"),
                baseline = SettingsFormFields(storeName = "ร้านยา"),
            ),
        )
    }
}
