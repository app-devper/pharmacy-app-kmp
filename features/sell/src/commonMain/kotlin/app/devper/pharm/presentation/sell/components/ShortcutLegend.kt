package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.common.ShortcutHint
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

private val SHORTCUTS = listOf(
    "F1" to "คีย์ลัดทั้งหมด",
    "F2" to "ค้นหายา",
    "F3" to "เลือกลูกค้า",
    "F4" to "ส่วนลดบิล",
    "F6" to "พักบิล",
    "F8" to "บิลที่พักไว้",
    "F9" to "ชำระเงิน",
    "Esc" to "ปิด / ยกเลิก",
)

@Composable
fun ShortcutLegend(open: Boolean, onClose: () -> Unit) {
    PharmModal(
        open = open,
        onDismiss = onClose,
        title = "คีย์ลัด",
        subtitle = "ทางลัดแป้นพิมพ์สำหรับหน้าขาย",
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            SHORTCUTS.forEach { (keys, description) ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ShortcutHint(label = keys, modifier = Modifier.width(64.dp))
                    Text(
                        text = description,
                        style = PharmText.body.copy(color = pharmTokens.colors.fg1),
                        modifier = Modifier.padding(start = 12.dp),
                    )
                }
            }
        }
    }
}
