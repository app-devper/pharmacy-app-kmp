package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.print.PharmReceiptPreview
import app.devper.pharm.ui.print.PharmReceiptStyle

@Composable
fun ReceiptDialog(
    template: ReceiptTemplate,
    onDismiss: () -> Unit,
    onVoid: (() -> Unit)? = null,
    onPrint: (() -> Unit)? = null,
) {
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = "ออกใบเสร็จสำเร็จ",
        subtitle = "เลขที่ ${template.billNo}",
        size = PharmModalSize.Md,
        footer = {
            if (onVoid != null) {
                PharmButton(
                    label = "ยกเลิกบิล",
                    onClick = onVoid,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                )
                Box(modifier = Modifier.size(1.dp))
            }
            if (onPrint != null) {
                PharmButton(
                    label = "🖨 พิมพ์",
                    onClick = onPrint,
                    variant = PharmButtonVariant.Secondary,
                    size = PharmButtonSize.Md,
                )
            }
            PharmButton(
                label = "บิลใหม่",
                onClick = onDismiss,
                size = PharmButtonSize.Md,
            )
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            PharmReceiptPreview(
                template = template,
                style = PharmReceiptStyle(width = null),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
