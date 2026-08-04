package app.devper.pharm.presentation.suppliers

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun SuppliersListToolbar(
    query: String,
    callbacks: SuppliersListCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    PharmListToolbar(
        searchValue = query,
        onSearchChange = callbacks.onSearchChange,
        searchPlaceholder = s.suppliersSearchPlaceholder,
        modifier = modifier,
        compactTopbarActions = true,
        actions = {
            PharmButton(
                label = s.suppliersAddCta,
                onClick = callbacks.onOpenAdd,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
            )
        },
    )
}
