package app.devper.pharm.presentation.reports

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun ReportsWindowChips(
    state: ReportsUiState,
    onSelectWindow: (DashboardWindow) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    PharmSingleSelectChips(
        chips = DashboardWindow.entries.map { PharmFilterChip(id = it.name, label = it.label(s)) },
        activeId = state.window.name,
        onSelect = { id -> onSelectWindow(DashboardWindow.valueOf(id)) },
        modifier = modifier,
        scrollable = false,
    )
}
