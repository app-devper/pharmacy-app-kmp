package app.devper.pharm.presentation.profile.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.profile.ProfileCallbacks
import app.devper.pharm.presentation.profile.ProfileUiState
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ProfilePasswordSection(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (!state.showPasswordPanel) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "ตั้งรหัสผ่านใหม่เพื่อความปลอดภัย",
                    style = PharmText.body.copy(color = pharmTokens.colors.fg2),
                    modifier = Modifier.weight(1f),
                )
                PharmButton(
                    label = "เปลี่ยน",
                    onClick = callbacks.onOpenPasswordPanel,
                    variant = PharmButtonVariant.Secondary,
                )
            }
        } else {
            FormField(label = "รหัสผ่านเดิม", required = true) {
                PharmTextField(
                    value = state.password.oldPassword,
                    onValueChange = callbacks.onOldPassword,
                    visualTransformation = PasswordVisualTransformation(),
                )
            }
            FormField(label = "รหัสผ่านใหม่", required = true) {
                PharmTextField(
                    value = state.password.newPassword,
                    onValueChange = callbacks.onNewPassword,
                    visualTransformation = PasswordVisualTransformation(),
                )
            }
            val confirmError = state.password.confirmPassword.isNotBlank() && !state.password.matches
            FormField(
                label = "ยืนยันรหัสผ่านใหม่",
                required = true,
                error = if (confirmError) "ไม่ตรงกับรหัสผ่านใหม่" else null,
            ) {
                PharmTextField(
                    value = state.password.confirmPassword,
                    onValueChange = callbacks.onConfirmPassword,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmError,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PharmButton(
                    label = if (state.passwordSaving) "กำลังเปลี่ยน…" else "บันทึก",
                    onClick = callbacks.onSubmitPasswordChange,
                    enabled = state.password.canSubmit && !state.passwordSaving,
                    variant = PharmButtonVariant.Primary,
                )
                PharmButton(
                    label = "ยกเลิก",
                    onClick = callbacks.onClosePasswordPanel,
                    variant = PharmButtonVariant.Ghost,
                )
            }
        }
        if (state.passwordSaved) {
            Text(
                text = "เปลี่ยนรหัสผ่านสำเร็จแล้ว",
                style = PharmText.body.copy(color = pharmTokens.colors.successFg),
            )
        }
    }
}
