package app.devper.pharm.presentation.sell.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun SkipKyConfirmSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PharmModal(
        open = true,
        onDismiss = onDismiss,
        title = pharmStrings.sellSkipKyTitle,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = pharmStrings.commonBack,
                onClick = onDismiss,
                variant = PharmButtonVariant.Ghost,
                size = PharmButtonSize.Sm,
            )
            PharmButton(
                label = pharmStrings.sellSkipKyConfirmCta,
                onClick = onConfirm,
                variant = PharmButtonVariant.Danger,
                size = PharmButtonSize.Sm,
            )
        },
    ) {
        Text(
            pharmStrings.sellSkipKyConfirm,
            style = PharmText.body,
        )
    }
}
