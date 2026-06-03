package app.devper.pharm.presentation.suppliers

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListToolbar

@Composable
internal fun SuppliersListToolbar(
    query: String,
    callbacks: SuppliersListCallbacks,
    modifier: Modifier = Modifier,
) {
    PharmListToolbar(
        title = "ซัพพลายเออร์",
        subtitle = "จัดการข้อมูลผู้ขายและบริษัทคู่ค้า",
        searchValue = query,
        onSearchChange = callbacks.onSearchChange,
        searchPlaceholder = "ค้นหาชื่อ / ผู้ติดต่อ / เบอร์โทร…",
        modifier = modifier,
        actions = {
            PharmButton(
                label = "เพิ่มซัพพลายเออร์",
                onClick = callbacks.onOpenAdd,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
            )
        },
    )
}
