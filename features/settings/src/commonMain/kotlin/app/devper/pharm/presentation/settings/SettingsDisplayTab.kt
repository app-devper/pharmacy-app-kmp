package app.devper.pharm.presentation.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SettingsDisplayTab(
    state: SettingsEditorUiState,
    editor: SettingsEditorCallbacks,
    strings: PharmStrings,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        SettingsChoiceRow(
            label = strings.settingsDisplayTheme,
            chips = listOf(
                PharmFilterChip(id = "light", label = strings.settingsThemeLight),
                PharmFilterChip(id = "dark", label = strings.settingsThemeDark),
                PharmFilterChip(id = "auto", label = strings.settingsThemeAuto),
            ),
            activeId = state.theme,
            onSelect = editor.onThemeChange,
        )
        SettingsChoiceRow(
            label = strings.settingsDisplayFontSize,
            chips = listOf(
                PharmFilterChip(id = "sm", label = strings.settingsFontSm),
                PharmFilterChip(id = "md", label = strings.settingsFontMd),
                PharmFilterChip(id = "lg", label = strings.settingsFontLg),
                PharmFilterChip(id = "xl", label = strings.settingsFontXl),
            ),
            activeId = state.fontSize,
            onSelect = editor.onFontSizeChange,
        )
        SettingsChoiceRow(
            label = strings.settingsDisplayDensity,
            chips = listOf(
                PharmFilterChip(id = "comfortable", label = strings.settingsDensityComfortable),
                PharmFilterChip(id = "compact", label = strings.settingsDensityCompact),
            ),
            activeId = state.density,
            onSelect = editor.onDensityChange,
        )
        SettingsChoiceRow(
            label = strings.settingsLocaleTitle,
            chips = listOf(
                PharmFilterChip(id = "th", label = strings.settingsLocaleTh),
                PharmFilterChip(id = "en", label = strings.settingsLocaleEn),
            ),
            activeId = state.locale,
            onSelect = editor.onLocaleChange,
        ) {
            if (state.localeChangeApplied) {
                Text(
                    text = strings.settingsLocaleAppliedInline,
                    style = PharmText.meta.copy(color = pharmTokens.colors.successFg),
                )
            }
        }
    }
}

@Composable
private fun SettingsChoiceRow(
    label: String,
    chips: List<PharmFilterChip>,
    activeId: String,
    onSelect: (String) -> Unit,
    footer: (@Composable () -> Unit)? = null,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = label, style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
        PharmSingleSelectChips(
            chips = chips,
            activeId = activeId,
            onSelect = onSelect,
            scrollable = false,
        )
        footer?.invoke()
    }
}
