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
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "ธีม", style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = ThemeChips,
                activeId = state.theme,
                onSelect = callbacks.onThemeChange,
                scrollable = false,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "ขนาดตัวอักษร", style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = FontSizeChips,
                activeId = state.fontSize,
                onSelect = callbacks.onFontSizeChange,
                scrollable = false,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "ความหนาแน่นตาราง", style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = DensityChips,
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
                    PharmFilterChip(id = "system", label = strings.settingsLocaleSystem),
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

private val ThemeChips = listOf(
    PharmFilterChip(id = "light", label = "สว่าง"),
    PharmFilterChip(id = "dark", label = "มืด"),
    PharmFilterChip(id = "auto", label = "อัตโนมัติ"),
)

private val FontSizeChips = listOf(
    PharmFilterChip(id = "sm", label = "เล็ก"),
    PharmFilterChip(id = "md", label = "ปกติ"),
    PharmFilterChip(id = "lg", label = "ใหญ่"),
    PharmFilterChip(id = "xl", label = "ใหญ่มาก"),
)

private val DensityChips = listOf(
    PharmFilterChip(id = "comfortable", label = "สบายตา"),
    PharmFilterChip(id = "compact", label = "กระชับ"),
)
