package app.devper.pharm.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun SettingsSaveBar(
    state: SettingsEditorUiState,
    onSave: () -> Unit,
    onMessageDismiss: () -> Unit,
) {
    val t = pharmTokens
    Column(modifier = Modifier.fillMaxWidth().background(t.colors.surfaceRaised)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                if (state.dirty) {
                    Text(
                        text = "มีการเปลี่ยนแปลง — แตะ \"บันทึก\" เพื่อยืนยัน",
                        style = PharmText.micro,
                    )
                }
            }
            if (state.saving) {
                Box(
                    modifier = Modifier.padding(horizontal = 12.dp).size(20.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp),
                        color = t.colors.accent,
                    )
                }
            } else {
                PharmButton(
                    label = "บันทึก",
                    onClick = onSave,
                    enabled = state.canSave,
                    size = PharmButtonSize.Sm,
                )
            }
        }

        state.message?.let { msg ->
            Box(modifier = Modifier.fillMaxWidth().background(t.colors.successBg)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = msg,
                        style = PharmText.bodySm.copy(color = t.colors.successFg),
                        modifier = Modifier.weight(1f),
                    )
                    PharmButton(
                        label = "ปิด",
                        onClick = onMessageDismiss,
                        variant = PharmButtonVariant.Ghost,
                        size = PharmButtonSize.Sm,
                    )
                }
            }
        }
    }
}
