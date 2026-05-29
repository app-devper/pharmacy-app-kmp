package app.devper.pharm.presentation.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.presentation.profile.components.ProfileDisplayPreferences
import app.devper.pharm.presentation.profile.components.ProfileFormSection
import app.devper.pharm.presentation.profile.components.ProfileHeaderCard
import app.devper.pharm.presentation.profile.components.ProfilePasswordSection
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun ProfileContent(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    val t = pharmTokens
    Box(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        if (state.loading && state.user == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                PharmCircularProgress(color = t.colors.accent)
            }
        } else {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .widthIn(max = 760.dp)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                state.user?.let { user -> ProfileHeaderCard(user) }
                ProfileCard(
                    title = "ข้อมูลส่วนตัว",
                    subtitle = "แก้ไขชื่อ, เบอร์โทร และอีเมลของบัญชีคุณ",
                ) {
                    ProfileFormSection(state, callbacks)
                }
                ProfileCard(
                    title = "เปลี่ยนรหัสผ่าน",
                    subtitle = "รหัสใหม่ต้องไม่น้อยกว่า 8 ตัวอักษร",
                ) {
                    ProfilePasswordSection(state, callbacks)
                }
                ProfileCard(
                    title = "การแสดงผล",
                    subtitle = "ปรับสำหรับเครื่องนี้เท่านั้น",
                ) {
                    ProfileDisplayPreferences(state, callbacks)
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
