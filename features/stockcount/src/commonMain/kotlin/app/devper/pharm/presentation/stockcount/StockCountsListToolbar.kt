package app.devper.pharm.presentation.stockcount

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.theme.PharmText

@Composable
internal fun StockCountsListToolbar(
    query: String,
    callbacks: StockCountsListCallbacks,
    modifier: Modifier = Modifier,
) {
    PharmListToolbar(
        title = "นับสต็อก",
        subtitle = "ประวัติรอบนับสต็อก และบันทึกการปรับยอด",
        searchValue = query,
        onSearchChange = callbacks.onSearchChange,
        searchPlaceholder = "ค้นหาเลขรอบ / หมายเหตุ…",
        titleStyle = PharmText.h2,
        modifier = modifier,
        actions = {
            PharmButton(
                label = "นับสต็อกใหม่",
                onClick = callbacks.onNewCount,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
            )
        },
    )
}
