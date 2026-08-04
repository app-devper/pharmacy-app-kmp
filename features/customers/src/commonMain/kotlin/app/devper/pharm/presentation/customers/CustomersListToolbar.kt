package app.devper.pharm.presentation.customers

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText

@Composable
internal fun CustomersListToolbar(
    query: String,
    callbacks: CustomersListCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    PharmListToolbar(
        subtitle = s.customersSubtitle,
        searchValue = query,
        onSearchChange = callbacks.onSearchChange,
        searchPlaceholder = s.customersSearchPlaceholder,
        titleStyle = PharmText.h2,
        modifier = modifier,
        compactTopbarActions = true,
        actions = {
            PharmButton(
                label = s.customersAddCta,
                onClick = callbacks.onOpenAdd,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
            )
        },
    )
}
