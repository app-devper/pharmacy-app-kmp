package app.devper.pharm.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.devper.pharm.common.AppVersion
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.Black
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.White
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.designsystem.PharmIcons
import androidx.compose.material3.Icon
import org.koin.compose.viewmodel.koinViewModel
import app.devper.pharm.ui.designsystem.PharmCircularProgress

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val t = pharmTokens

    LaunchedEffect(state.loggedInUser) {
        if (state.loggedInUser != null) onLoggedIn()
    }

    val backgroundBrush = Brush.linearGradient(
        colors = if (androidx.compose.foundation.isSystemInDarkTheme()) {
            listOf(t.colors.bgPage, Black)
        } else {
            listOf(t.colors.accentBgSoft, t.colors.bgPage)
        }
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush)
            .padding(16.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 384.dp)
                .fillMaxWidth()
                .clip(t.shapes.xl)
                .background(t.colors.surface, t.shapes.xl)
                .padding(32.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
            BrandHeader()
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                FormField(label = "ชื่อผู้ใช้") {
                    PharmTextField(
                        value = state.username,
                        onValueChange = viewModel::onUsernameChange,
                        placeholder = "กรอกชื่อผู้ใช้",
                        enabled = !state.loading,
                        keyboardType = KeyboardType.Email,
                    )
                }
                FormField(label = "รหัสผ่าน") {
                    PharmTextField(
                        value = state.password,
                        onValueChange = viewModel::onPasswordChange,
                        placeholder = "กรอกรหัสผ่าน",
                        enabled = !state.loading,
                        keyboardType = KeyboardType.Password,
                        visualTransformation = PasswordVisualTransformation(),
                    )
                }
            }
            PharmButton(
                onClick = viewModel::submit,
                modifier = Modifier.fillMaxWidth(),
                size = PharmButtonSize.Lg,
                enabled = !state.loading,
            ) {
                if (state.loading) {
                    Box(
                        modifier = Modifier.size(16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        PharmCircularProgress(
                            color = t.colors.surface,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp),
                        )
                    }
                    Text(
                        text = "กำลังเข้าสู่ระบบ…",
                        style = PharmText.buttonMd.copy(color = t.colors.surface),
                    )
                } else {
                    Text(
                        text = "เข้าสู่ระบบ",
                        style = PharmText.buttonMd.copy(color = t.colors.surface),
                    )
                }
            }
            Text(
                text = "v${AppVersion.name} · เชื่อมต่อกับ Um-Api",
                style = PharmText.micro.copy(color = t.colors.fgMuted),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
            )
        }
    }

    ErrorBottomSheet(
        message = state.error,
        onDismiss = viewModel::dismissError,
    )
}

@Composable
private fun BrandHeader() {
    val t = pharmTokens
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(t.shapes.xl)
                .background(
                    Brush.linearGradient(
                        colors = listOf(t.colors.accent, t.colors.accentHover)
                    )
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = PharmIcons.Pill,
                contentDescription = null,
                tint = White,
                modifier = Modifier.size(32.dp),
            )
        }
        Text(
            text = "ร้านยา เฮลท์ตี้ฟาร์ม",
            style = PharmText.body.copy(
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = t.colors.fg1,
            ),
        )
        Text(
            text = "ระบบ POS ร้านขายยา",
            style = PharmText.meta,
        )
    }
}
