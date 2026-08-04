package app.devper.pharm.presentation.sell.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmBottomSheet
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.i18n.pharmStrings

@Composable
fun SkipKyConfirmSheet(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    PharmBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(text = pharmStrings.sellSkipKyTitle, style = PharmText.h2)
            Text(text = pharmStrings.sellSkipKyConfirm, style = PharmText.body)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                PharmButton(
                    label = pharmStrings.commonBack,
                    onClick = onDismiss,
                    variant = PharmButtonVariant.Ghost,
                    size = PharmButtonSize.Md,
                    modifier = Modifier.weight(1f),
                )
                PharmButton(
                    label = pharmStrings.sellSkipKyConfirmCta,
                    onClick = onConfirm,
                    variant = PharmButtonVariant.Danger,
                    size = PharmButtonSize.Md,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
