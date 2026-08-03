package app.devper.pharm.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.presentation.settings.i18n.localizeSettings
import app.devper.pharm.ui.common.ReloadOnResume
import app.devper.pharm.ui.common.pharmClickable
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.designsystem.PharmSaveAction
import app.devper.pharm.ui.designsystem.PharmTab
import app.devper.pharm.ui.designsystem.PharmTabBar
import app.devper.pharm.ui.i18n.localize
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import org.koin.compose.viewmodel.koinViewModel

private val SettingsRailWidth = 200.dp
private val SettingsCompactBreakpoint = 720.dp
private val SettingsDialogMaxWidth = 800.dp
private val SettingsDialogMaxHeight = 680.dp

internal enum class SettingsDialogLayout { Compact, Split }

internal fun settingsDialogLayout(width: Dp): SettingsDialogLayout =
    if (width < SettingsCompactBreakpoint) SettingsDialogLayout.Compact else SettingsDialogLayout.Split

@Composable
fun SettingsDialog(
    open: Boolean,
    onDismiss: () -> Unit,
    viewModel: SettingsEditorViewModel? = null,
) {
    if (!open) return
    val resolvedViewModel = viewModel ?: koinViewModel()
    val state by resolvedViewModel.state.collectAsStateWithLifecycle()
    ReloadOnResume(resolvedViewModel::reload)

    SettingsDialogSurface(
        state = state,
        editor = resolvedViewModel.settingsEditorCallbacks(),
        onDismiss = onDismiss,
    )
}

@Composable
internal fun SettingsDialogSurface(
    state: SettingsEditorUiState,
    editor: SettingsEditorCallbacks,
    onDismiss: () -> Unit,
) {
    val strings = pharmStrings
    val validation = rememberSettingsValidation(state)
    val tabs = SettingsTab.entries.map { tab ->
        PharmTab(id = tab.name, label = labelFor(tab, strings))
    }

    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = strings.navSettings,
        size = PharmModalSize.Xl,
        dismissEnabled = !state.saving,
        fillHeight = true,
        dialogMaxWidth = SettingsDialogMaxWidth,
        dialogMaxHeight = SettingsDialogMaxHeight,
        contentScrollable = false,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            state.messageState?.let { message ->
                SettingsMessageBanner(
                    message = message.localize(strings),
                    onDismiss = editor.onDismissMessage,
                )
            }
            BoxWithConstraints(modifier = Modifier.weight(1f).fillMaxWidth()) {
                if (settingsDialogLayout(maxWidth) == SettingsDialogLayout.Compact) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        PharmTabBar(
                            tabs = tabs,
                            activeId = state.tab.name,
                            onSelect = { id -> editor.onSelectTab(SettingsTab.valueOf(id)) },
                        )
                        SettingsPane(
                            state = state,
                            editor = editor,
                            validation = validation,
                            modifier = Modifier.weight(1f).fillMaxWidth(),
                        )
                    }
                } else {
                    Row(modifier = Modifier.fillMaxSize()) {
                        SettingsCategoryRail(
                            activeTab = state.tab,
                            onSelect = editor.onSelectTab,
                        )
                        SettingsPane(
                            state = state,
                            editor = editor,
                            validation = validation,
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }

    ErrorBottomSheet(
        message = state.errorState?.localizeSettings(strings),
        onDismiss = editor.onDismissError,
    )
}

@Composable
private fun SettingsCategoryRail(
    activeTab: SettingsTab,
    onSelect: (SettingsTab) -> Unit,
) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .width(SettingsRailWidth + 1.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .width(SettingsRailWidth)
                .fillMaxHeight()
                .background(t.colors.surface)
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(t.spacing.s0_5),
        ) {
            SettingsTab.entries.forEach { tab ->
                SettingsCategoryItem(
                    tab = tab,
                    active = tab == activeTab,
                    onClick = { onSelect(tab) },
                )
            }
        }
        Box(modifier = Modifier.width(1.dp).fillMaxHeight().background(t.colors.divider))
    }
}

@Composable
private fun SettingsCategoryItem(
    tab: SettingsTab,
    active: Boolean,
    onClick: () -> Unit,
) {
    val t = pharmTokens
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .clip(t.shapes.lg)
            .background(if (active) t.colors.sidebarItemActive else t.colors.surface)
            .pharmClickable(role = Role.Tab, shape = t.shapes.lg, onClick = onClick)
            .semantics {
                role = Role.Tab
                selected = active
            }
            .padding(horizontal = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            imageVector = settingsTabIcon(tab),
            contentDescription = null,
            tint = if (active) t.colors.fg1 else t.colors.fg2,
            modifier = Modifier.size(18.dp),
        )
        Text(
            text = labelFor(tab, pharmStrings),
            style = PharmText.bodySm.copy(color = if (active) t.colors.fg1 else t.colors.fg2),
        )
    }
}

@Composable
private fun SettingsPane(
    state: SettingsEditorUiState,
    editor: SettingsEditorCallbacks,
    validation: SettingsValidationState,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val strings = pharmStrings
    Column(modifier = modifier.background(t.colors.surface)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = labelFor(state.tab, strings), style = PharmText.h2)
                Text(
                    text = if (state.dirty) strings.settingsDirtySubtitle else strings.settingsToolbarSubtitle,
                    style = PharmText.meta.copy(color = t.colors.fgMuted),
                )
            }
            PharmSaveAction(
                saving = state.saving,
                canSubmit = state.canSave,
                onSubmit = editor.onSubmit,
                onInvalidSubmit = validation.invalidSubmit(state, editor),
            )
        }
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
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
                    showValidation = validation.attempt > 0,
                    focus = validation.focus,
                )
            }
        }
    }
}

private fun settingsTabIcon(tab: SettingsTab): ImageVector = when (tab) {
    SettingsTab.Store -> PharmIcons.Settings
    SettingsTab.Receipt -> PharmIcons.Print
    SettingsTab.Stock -> PharmIcons.Stock
    SettingsTab.Pharmacist -> PharmIcons.Person
    SettingsTab.Ky -> PharmIcons.KyForms
}
