package app.devper.pharm.presentation.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.devper.pharm.common.AppVersion
import app.devper.pharm.presentation.auth.exception.LoginUiStateError
import app.devper.pharm.presentation.auth.i18n.localizeLogin
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmFilterChip
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmSingleSelectChips
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.Black
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.White
import app.devper.pharm.ui.theme.pharmTokens
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    viewModel: LoginViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val t = pharmTokens
    val usernameFocus = remember { FocusRequester() }
    val passwordFocus = remember { FocusRequester() }
    val submit = {
        viewModel.submit()
        when {
            state.username.isBlank() -> usernameFocus.requestFocus()
            state.password.isBlank() -> passwordFocus.requestFocus()
        }
    }

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

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBrush),
    ) {
        val strings = pharmStrings
        val compact = maxWidth < 360.dp
        Column(
            modifier = Modifier
                .fillMaxSize()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(if (compact) 12.dp else 16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 384.dp)
                    .fillMaxWidth()
                    .clip(t.shapes.xl)
                    .background(t.colors.surface, t.shapes.xl)
                    .border(1.dp, t.colors.borderSubtle, t.shapes.xl)
                    .padding(if (compact) 20.dp else 32.dp),
                verticalArrangement = Arrangement.spacedBy(if (compact) 16.dp else 20.dp),
            ) {
                LocaleSwitcherRow(
                    strings = strings,
                    activeLocale = state.locale,
                    onLocaleChange = viewModel::onLocaleChange,
                )
                BrandHeader(strings = strings)
                if (state.sessionExpired) {
                    Text(
                        text = strings.loginSessionExpired,
                        style = PharmText.micro.copy(color = t.colors.warningFg),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(t.shapes.md)
                            .background(t.colors.warningBg)
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                    )
                }
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormField(
                        label = strings.loginUsernameLabel,
                        required = true,
                        error = if (state.usernameMissing) strings.loginUsernameRequired else null,
                    ) {
                        PharmTextField(
                            value = state.username,
                            onValueChange = viewModel::onUsernameChange,
                            placeholder = strings.loginUsernamePlaceholder,
                            enabled = !state.loading,
                            isError = state.usernameMissing,
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Next,
                            onImeAction = passwordFocus::requestFocus,
                            focusRequester = usernameFocus,
                        )
                    }
                    FormField(
                        label = strings.loginPasswordLabel,
                        required = true,
                        error = if (state.passwordMissing) strings.loginPasswordRequired else null,
                    ) {
                        PharmTextField(
                            value = state.password,
                            onValueChange = viewModel::onPasswordChange,
                            placeholder = strings.loginPasswordPlaceholder,
                            enabled = !state.loading,
                            isError = state.passwordMissing,
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done,
                            onImeAction = submit,
                            focusRequester = passwordFocus,
                            visualTransformation = PasswordVisualTransformation(),
                        )
                    }
                }
                PharmButton(
                    label = if (state.loading) strings.loginSubmitting else strings.loginSubmit,
                    onClick = submit,
                    modifier = Modifier.fillMaxWidth(),
                    size = PharmButtonSize.Lg,
                    loading = state.loading,
                )
                Text(
                    text = "v${AppVersion.name} · ${strings.loginVersionPrefix}",
                    style = PharmText.micro.copy(color = t.colors.fgMuted),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    }

    ErrorBottomSheet(
        message = state.errorState
            ?.takeUnless { it is LoginUiStateError.RequiredFields }
            ?.localizeLogin(pharmStrings),
        onDismiss = viewModel::dismissError,
    )
}

@Composable
private fun BrandHeader(strings: app.devper.pharm.ui.i18n.PharmStrings) {
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
            text = strings.loginBrandName,
            style = PharmText.h1.copy(color = t.colors.fg1),
        )
        Text(
            text = strings.loginBrandTagline,
            style = PharmText.meta,
        )
    }
}

@Composable
private fun LocaleSwitcherRow(
    strings: app.devper.pharm.ui.i18n.PharmStrings,
    activeLocale: String,
    onLocaleChange: (String) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.CenterEnd,
    ) {
        PharmSingleSelectChips(
            chips = listOf(
                PharmFilterChip(id = "th", label = strings.settingsLocaleTh),
                PharmFilterChip(id = "en", label = strings.settingsLocaleEn),
            ),
            activeId = activeLocale,
            onSelect = onLocaleChange,
            scrollable = false,
        )
    }
}
