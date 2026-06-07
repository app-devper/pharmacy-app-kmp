package app.devper.pharm.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.profile.ProfileCallbacks
import app.devper.pharm.presentation.profile.ProfileUiState
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ProfileDisplayPreferences(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    val strings = pharmStrings
    val themeChips = listOf(
        PharmFilterChip(id = "light", label = strings.profileThemeLight),
        PharmFilterChip(id = "dark", label = strings.profileThemeDark),
        PharmFilterChip(id = "auto", label = strings.profileThemeAuto),
    )
    val fontSizeChips = listOf(
        PharmFilterChip(id = "sm", label = strings.profileFontSm),
        PharmFilterChip(id = "md", label = strings.profileFontMd),
        PharmFilterChip(id = "lg", label = strings.profileFontLg),
        PharmFilterChip(id = "xl", label = strings.profileFontXl),
    )
    val densityChips = listOf(
        PharmFilterChip(id = "comfortable", label = strings.profileDensityComfortable),
        PharmFilterChip(id = "compact", label = strings.profileDensityCompact),
    )
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = strings.profileDisplayTheme, style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = themeChips,
                activeId = state.theme,
                onSelect = callbacks.onThemeChange,
                scrollable = false,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = strings.profileDisplayFontSize, style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = fontSizeChips,
                activeId = state.fontSize,
                onSelect = callbacks.onFontSizeChange,
                scrollable = false,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = strings.profileDisplayDensity, style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = densityChips,
                activeId = state.density,
                onSelect = callbacks.onDensityChange,
                scrollable = false,
            )
        }
        val strings = pharmStrings
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = strings.settingsLocaleTitle, style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = listOf(
                    PharmFilterChip(id = "th", label = strings.settingsLocaleTh),
                    PharmFilterChip(id = "en", label = strings.settingsLocaleEn),
                ),
                activeId = state.locale,
                onSelect = callbacks.onLocaleChange,
                scrollable = false,
            )
            state.localeChangeMessage?.let { _ ->
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(text = strings.settingsLocaleAppliedInline, style = PharmText.meta.copy(color = pharmTokens.colors.successFg))
                    Text(text = strings.settingsLocaleRestartHint, style = PharmText.meta.copy(color = pharmTokens.colors.fg2))
                }
            }
        }
    }
}

