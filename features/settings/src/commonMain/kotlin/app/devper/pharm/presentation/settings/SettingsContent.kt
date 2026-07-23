package app.devper.pharm.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.settings.i18n.localizeSettings
import app.devper.pharm.ui.i18n.localize
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmFormCard
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmTab
import app.devper.pharm.ui.designsystem.PharmTabBar
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import androidx.compose.ui.tooling.preview.Preview

private fun labelFor(tab: SettingsTab, s: PharmStrings): String = when (tab) {
    SettingsTab.Store      -> s.settingsTabStore
    SettingsTab.Receipt    -> s.settingsTabReceipt
    SettingsTab.Stock      -> s.settingsTabStock
    SettingsTab.Pharmacist -> s.settingsTabPharmacist
    SettingsTab.Ky         -> s.settingsTabKy
}

@Composable
fun SettingsContent(
    state: SettingsEditorUiState,
    editor: SettingsEditorCallbacks = SettingsEditorCallbacks(),
) {
    val strings = pharmStrings
    val tabs = SettingsTab.entries.map { PharmTab(id = it.name, label = labelFor(it, strings)) }
    val focus = remember { SettingsFocusRequesters() }
    var validationAttempt by remember { mutableIntStateOf(0) }

    LaunchedEffect(validationAttempt, state.tab) {
        if (validationAttempt == 0) return@LaunchedEffect
        when (state.tab) {
            SettingsTab.Store -> when {
                !state.form.storeNameValid -> focus.storeName.requestFocus()
                !state.form.timezoneValid -> focus.timezone.requestFocus()
            }
            SettingsTab.Stock -> when {
                !state.form.stockLowThresholdValid -> focus.stockLowThreshold.requestFocus()
                !state.form.stockReorderDaysValid -> focus.stockReorderDays.requestFocus()
                !state.form.stockReorderLookaheadValid -> focus.stockReorderLookahead.requestFocus()
                !state.form.stockExpiringDaysValid -> focus.stockExpiringDays.requestFocus()
            }
            SettingsTab.Receipt,
            SettingsTab.Pharmacist,
            SettingsTab.Ky -> Unit
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        PharmListToolbar(
            title = strings.navSettings,
            subtitle = if (state.dirty) strings.settingsDirtySubtitle else strings.settingsToolbarSubtitle,
            compactTopbarActions = true,
            actions = {
                PharmSaveAction(
                    saving = state.saving,
                    canSubmit = state.canSave,
                    onSubmit = editor.onSubmit,
                    onInvalidSubmit = if (state.dirty && !state.loading) {
                        {
                            validationAttempt++
                            state.form.firstInvalidTab?.let(editor.onSelectTab)
                        }
                    } else null,
                )
            },
        )

        state.messageState?.let { msg ->
            SettingsMessageBanner(message = msg.localize(strings), onDismiss = editor.onDismissMessage)
        }

        PharmTabBar(
            tabs = tabs,
            activeId = state.tab.name,
            onSelect = { id -> editor.onSelectTab(SettingsTab.valueOf(id)) },
        )

        Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
            if (state.loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    PharmCircularProgress()
                }
            } else {
                SettingsTabBody(
                    state = state,
                    editor = editor,
                    strings = strings,
                    showValidation = validationAttempt > 0,
                    focus = focus,
                )
            }
        }
    }

    ErrorBottomSheet(message = state.errorState?.localizeSettings(strings), onDismiss = editor.onDismissError)
}

@Composable
private fun SettingsMessageBanner(message: String, onDismiss: () -> Unit) {
    val t = pharmTokens
    val strings = pharmStrings
    Box(modifier = Modifier.fillMaxWidth().background(t.colors.successBg)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = message,
                style = PharmText.bodySm.copy(color = t.colors.successFg),
                modifier = Modifier.weight(1f),
            )
            PharmButton(
                label = strings.commonClose,
                onClick = onDismiss,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
        }
    }
}

@Composable
private fun SettingsTabBody(
    state: SettingsEditorUiState,
    editor: SettingsEditorCallbacks,
    strings: PharmStrings,
    showValidation: Boolean,
    focus: SettingsFocusRequesters,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item(key = state.tab.name) {
            PharmFormCard(title = labelFor(state.tab, strings)) {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    when (state.tab) {
                        SettingsTab.Store      -> SettingsStoreTab(state, editor, showValidation, focus)
                        SettingsTab.Receipt    -> SettingsReceiptTab(state, editor, showValidation)
                        SettingsTab.Stock      -> SettingsStockTab(state, editor, showValidation, focus)
                        SettingsTab.Pharmacist -> SettingsPharmacistTab(state, editor)
                        SettingsTab.Ky         -> SettingsKyTab(state, editor)
                    }
                }
            }
        }
    }
}

internal class SettingsFocusRequesters {
    val storeName = FocusRequester()
    val timezone = FocusRequester()
    val stockLowThreshold = FocusRequester()
    val stockReorderDays = FocusRequester()
    val stockReorderLookahead = FocusRequester()
    val stockExpiringDays = FocusRequester()
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
