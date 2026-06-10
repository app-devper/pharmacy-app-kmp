package app.devper.pharm.presentation.movements

import app.devper.pharm.presentation.movements.i18n.localizedLabel

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmFilterChips

@Composable
internal fun MovementsTypeChips(
    activeIds: Set<String>,
    onToggle: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    val chips = remember(s) {
        MovementsTypeCatalog.specs.map { spec ->
            PharmFilterChip(
                id = spec.id,
                label = spec.localizedLabel(s),
                icon = spec.icon,
            )
        }
    }
    PharmFilterChips(
        chips = chips,
        activeIds = activeIds,
        onToggle = onToggle,
        modifier = modifier,
    )
}
