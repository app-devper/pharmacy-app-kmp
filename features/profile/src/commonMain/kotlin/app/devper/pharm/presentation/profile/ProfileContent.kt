package app.devper.pharm.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun ProfileContent(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        if (state.loading && state.user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = t.colors.accent)
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 760.dp)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                state.user?.let { user -> ProfileHero(user) }
                ProfileCard(
                    title = "ข้อมูลส่วนตัว",
                    subtitle = "แก้ไขชื่อ, เบอร์โทร และอีเมลของบัญชีคุณ",
                ) {
                    ProfileFormBlock(state, callbacks)
                }
                ProfileCard(
                    title = "เปลี่ยนรหัสผ่าน",
                    subtitle = "รหัสใหม่ต้องไม่น้อยกว่า 8 ตัวอักษร",
                ) {
                    PasswordBlock(state, callbacks)
                }
                ProfileCard(
                    title = "การแสดงผล",
                    subtitle = "ปรับสำหรับเครื่องนี้เท่านั้น",
                ) {
                    DisplayPreferencesBlock(state, callbacks)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
    ErrorBottomSheet(message = state.passwordError, onDismiss = callbacks.onDismissPasswordError)
}

@Composable
private fun ProfileCard(
    title: String,
    subtitle: String? = null,
    content: @Composable () -> Unit,
) {
    val t = pharmTokens
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(text = title, style = PharmText.h2)
            if (subtitle != null) {
                Text(text = subtitle, style = PharmText.meta.copy(color = t.colors.fgMuted))
            }
        }
        content()
    }
}

@Composable
private fun ProfileHero(user: UmUser) {
    val t = pharmTokens
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Box(
            modifier = Modifier.size(64.dp).clip(CircleShape).background(t.colors.accent),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = user.initials, style = PharmText.h1.copy(color = t.colors.surface))
        }
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(text = user.displayName, style = PharmText.h1)
            Text(text = "@${user.username}", style = PharmText.body.copy(color = t.colors.fgMuted))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                PharmBadge(text = user.role.label(), tone = user.role.tone())
                PharmBadge(
                    text = if (user.status.isActive) "ใช้งาน" else "ปิดใช้งาน",
                    tone = if (user.status.isActive) PharmBadgeTone.Green else PharmBadgeTone.Gray,
                )
            }
        }
    }
}

@Composable
private fun ProfileFormBlock(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    val t = pharmTokens
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        FormField(label = "ชื่อ", required = true) {
            PharmTextField(value = state.form.firstName, onValueChange = callbacks.onFirstName)
        }
        FormField(label = "นามสกุล") {
            PharmTextField(value = state.form.lastName, onValueChange = callbacks.onLastName)
        }
        FormField(label = "เบอร์โทร") {
            PharmTextField(
                value = state.form.phone,
                onValueChange = callbacks.onPhone,
                keyboardType = KeyboardType.Phone,
            )
        }
        FormField(label = "อีเมล") {
            PharmTextField(
                value = state.form.email,
                onValueChange = callbacks.onEmail,
                keyboardType = KeyboardType.Email,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PharmButton(
                label = if (state.saving) "กำลังบันทึก…" else "บันทึก",
                onClick = callbacks.onSubmit,
                enabled = state.canSubmit,
                variant = PharmButtonVariant.Primary,
            )
            if (state.saved) {
                Text(
                    text = "บันทึกแล้ว",
                    style = PharmText.body.copy(color = t.colors.successFg),
                    modifier = Modifier.padding(start = 8.dp).align(Alignment.CenterVertically),
                )
            }
        }
    }
}

@Composable
private fun PasswordBlock(
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

@Composable
private fun DisplayPreferencesBlock(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "ธีม", style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = ThemeChips,
                activeId = state.theme,
                onSelect = callbacks.onThemeChange,
                scrollable = false,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "ขนาดตัวอักษร", style = PharmText.bodySm.copy(color = pharmTokens.colors.fg2))
            PharmSingleSelectChips(
                chips = FontSizeChips,
                activeId = state.fontSize,
                onSelect = callbacks.onFontSizeChange,
                scrollable = false,
            )
        }
    }
}

private val ThemeChips = listOf(
    PharmFilterChip(id = "light", label = "สว่าง"),
    PharmFilterChip(id = "dark", label = "มืด"),
    PharmFilterChip(id = "auto", label = "อัตโนมัติ"),
)

private val FontSizeChips = listOf(
    PharmFilterChip(id = "sm", label = "เล็ก"),
    PharmFilterChip(id = "md", label = "ปกติ"),
    PharmFilterChip(id = "lg", label = "ใหญ่"),
    PharmFilterChip(id = "xl", label = "ใหญ่มาก"),
)

private fun Role.label(): String = when (this) {
    Role.SUPER   -> "Super Admin"
    Role.ADMIN   -> "Admin"
    Role.MANAGER -> "Manager"
    Role.USER    -> "User"
    Role.UNKNOWN -> "-"
}

private fun Role.tone(): PharmBadgeTone = when (this) {
    Role.SUPER   -> PharmBadgeTone.Purple
    Role.ADMIN   -> PharmBadgeTone.Blue
    Role.MANAGER -> PharmBadgeTone.Amber
    Role.USER    -> PharmBadgeTone.Gray
    Role.UNKNOWN -> PharmBadgeTone.Gray
}

private val PreviewUser = UmUser(
    id = "u1",
    firstName = "ปรียา",
    lastName = "ใจดี",
    username = "pharm01",
    clientId = "001",
    role = Role.ADMIN,
    status = UmStatus.ACTIVE,
    phone = "081-234-5678",
    email = "preeya@healthypharm.co.th",
    createdDate = "",
    updatedDate = "",
)

@Preview
@Composable
private fun ProfileContent_Loaded_Preview() {
    PharmacyTheme {
        ProfileContent(
            state = ProfileUiState(
                user = PreviewUser,
                form = ProfileFormFields(
                    firstName = PreviewUser.firstName,
                    lastName = PreviewUser.lastName,
                    phone = PreviewUser.phone,
                    email = PreviewUser.email,
                ),
            ),
            callbacks = ProfileCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun ProfileContent_PasswordPanel_Preview() {
    PharmacyTheme {
        ProfileContent(
            state = ProfileUiState(
                user = PreviewUser,
                form = ProfileFormFields(
                    firstName = PreviewUser.firstName,
                    lastName = PreviewUser.lastName,
                    phone = PreviewUser.phone,
                    email = PreviewUser.email,
                ),
                showPasswordPanel = true,
                password = PasswordFormFields(oldPassword = "secret"),
            ),
            callbacks = ProfileCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun ProfileContent_Loading_Preview() {
    PharmacyTheme {
        ProfileContent(
            state = ProfileUiState(loading = true),
            callbacks = ProfileCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun ProfileContent_DisplayPrefs_Preview() {
    PharmacyTheme {
        ProfileContent(
            state = ProfileUiState(
                user = PreviewUser,
                form = ProfileFormFields(
                    firstName = PreviewUser.firstName,
                    lastName = PreviewUser.lastName,
                    phone = PreviewUser.phone,
                    email = PreviewUser.email,
                ),
                theme = "dark",
                fontSize = "lg",
            ),
            callbacks = ProfileCallbacks.Preview,
        )
    }
}
