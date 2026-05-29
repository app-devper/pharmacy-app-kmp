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
