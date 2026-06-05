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

@Composable
internal fun ImportsListToolbar(
    query: String,
    draftCount: Int,
    callbacks: ImportsListCallbacks,
    modifier: Modifier = Modifier,
) {
    PharmListToolbar(
        title = "นำเข้าสินค้า",
        subtitle = "จัดการใบนำเข้า / รับสินค้าเข้าสต็อก",
        searchValue = query,
        onSearchChange = callbacks.onSearchChange,
        searchPlaceholder = "ค้นหาเลขที่ / ผู้ขาย…",
        modifier = modifier,
        badge = {
            if (draftCount > 0) {
                PharmBadge(
                    text = "$draftCount แบบร่าง",
                    tone = PharmBadgeTone.Amber,
                    size = PharmBadgeSize.Sm,
                )
            }
        },
        actions = {
            PharmButton(
                label = "สร้างใบนำเข้า",
                onClick = callbacks.onCreateImport,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
            )
        },
    )
}
