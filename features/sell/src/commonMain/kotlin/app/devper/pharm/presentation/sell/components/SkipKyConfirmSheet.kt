package app.devper.pharm.presentation.sell.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText

@Composable
fun SkipKyConfirmSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = "ข้ามการบันทึก ขย.?",
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = "ย้อนกลับ",
                onClick = onDismiss,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = "ยืนยันข้าม",
                onClick = onConfirm,
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(
            "การขายจะถูกบันทึกโดยไม่มีข้อมูลผู้สั่ง/ผู้ซื้อ และอาจถูกตรวจสอบเพื่อ compliance",
            style = PharmText.body,
        )
    }
}
