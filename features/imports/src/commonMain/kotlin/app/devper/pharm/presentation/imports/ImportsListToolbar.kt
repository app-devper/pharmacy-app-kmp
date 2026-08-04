package app.devper.pharm.presentation.imports

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
internal fun ImportsListToolbar(
    query: String,
    draftCount: Int,
    callbacks: ImportsListCallbacks,
    modifier: Modifier = Modifier,
) {
    val s = pharmStrings
    PharmListToolbar(
        subtitle = s.importsSubtitle,
        searchValue = query,
        onSearchChange = callbacks.onSearchChange,
        searchPlaceholder = s.importsSearchPlaceholder,
        modifier = modifier,
        compactTopbarActions = true,
        badge = {
            if (draftCount > 0) {
                PharmBadge(
                    text = "$draftCount ${s.importsStatusDraft}",
                    tone = PharmBadgeTone.Amber,
                    size = PharmBadgeSize.Sm,
                )
            }
        },
        actions = {
            PharmButton(
                label = s.importsAddCta,
                onClick = callbacks.onCreateImport,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
            )
        },
    )
}
