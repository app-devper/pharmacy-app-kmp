package app.devper.pharm.presentation.imports

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun ImportsListToolbar(
    query: String,
    draftCount: Int,
    callbacks: ImportsListCallbacks,
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f).widthIn(min = 200.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(text = "นำเข้าสินค้า", style = PharmText.h1)
            Text(
                text = "จัดการใบนำเข้า / รับสินค้าเข้าสต็อก",
                style = PharmText.micro.copy(color = t.colors.fgMuted),
            )
        }
        if (draftCount > 0) {
            PharmBadge(
                text = "$draftCount แบบร่าง",
                tone = PharmBadgeTone.Amber,
                size = PharmBadgeSize.Sm,
            )
        }
        Box(modifier = Modifier.widthIn(min = 200.dp, max = 280.dp)) {
            PharmTextField(
                value = query,
                onValueChange = callbacks.onSearchChange,
                placeholder = "ค้นหาเลขที่ / ผู้ขาย…",
            )
        }
        PharmButton(
            label = "สร้างใบนำเข้า",
            onClick = callbacks.onCreateImport,
            size = PharmButtonSize.Sm,
            leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
        )
    }
}
