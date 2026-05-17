package app.devper.pharm.presentation.customers

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
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun CustomersListToolbar(
    query: String,
    callbacks: CustomersListCallbacks,
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
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "ลูกค้า", style = PharmText.h2)
            Text(
                text = "จัดการข้อมูลลูกค้าและประวัติการซื้อ",
                style = PharmText.micro.copy(color = t.colors.fgMuted),
            )
        }
        Box(modifier = Modifier.widthIn(min = 200.dp, max = 280.dp)) {
            PharmTextField(
                value = query,
                onValueChange = callbacks.onSearchChange,
                placeholder = "ค้นหาชื่อ / เบอร์โทร…",
            )
        }
        PharmButton(
            label = "เพิ่มลูกค้า",
            onClick = callbacks.onOpenAdd,
            size = PharmButtonSize.Sm,
            leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
        )
    }
}
