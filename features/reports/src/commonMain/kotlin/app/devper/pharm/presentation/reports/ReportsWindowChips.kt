package app.devper.pharm.presentation.reports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        PharmSingleSelectChips(
            chips = DashboardWindow.entries.map { PharmFilterChip(id = it.name, label = it.label(s)) },
            activeId = state.window.name,
            onSelect = { id -> onSelectWindow(DashboardWindow.valueOf(id)) },
            scrollable = false,
        )
    }
}
