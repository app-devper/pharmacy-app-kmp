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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.presentation.profile.components.ProfileDisplayPreferences
import app.devper.pharm.presentation.profile.components.ProfileFormSection
import app.devper.pharm.presentation.profile.components.ProfileHeaderCard
import app.devper.pharm.presentation.profile.components.ProfilePasswordSection
import app.devper.pharm.presentation.profile.i18n.localizeProfile
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.PharmCircularProgress
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun ProfileContent(
    state: ProfileUiState,
    callbacks: ProfileCallbacks,
) {
    val t = pharmTokens
    val strings = pharmStrings
    val loadingEmpty = state.loading && state.user == null
    Column(modifier = Modifier.fillMaxSize().background(t.colors.bgPage)) {
        PharmListToolbar(
            title = strings.profileTitle,
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (loadingEmpty) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    PharmCircularProgress(color = t.colors.accent)
                }
            } else {
                state.user?.let { user -> ProfileHeaderCard(user) }
                ProfileCard(
                    title = strings.profileSectionPersonal,
                    subtitle = strings.profileSectionPersonalSubtitle,
                ) {
                    ProfileFormSection(state, callbacks)
                }
                ProfileCard(
                    title = strings.profileSectionPassword,
                    subtitle = strings.profileSectionPasswordSubtitle,
                ) {
                    ProfilePasswordSection(state, callbacks)
                }
                ProfileCard(
                    title = strings.profileSectionDisplay,
                    subtitle = strings.profileSectionDisplaySubtitle,
                ) {
                    ProfileDisplayPreferences(state, callbacks)
                }
                Spacer(Modifier.height(8.dp))
            }
        }
    }
    ErrorBottomSheet(message = state.errorState?.localizeProfile(pharmStrings), onDismiss = callbacks.onDismissError)
    ErrorBottomSheet(message = state.passwordErrorState?.localizeProfile(pharmStrings), onDismiss = callbacks.onDismissPasswordError)
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

private val sampleProfileUser = UmUser(
    id = "u1",
    firstName = "สมชาย",
    lastName = "ใจดี",
    username = "somchai",
    clientId = "001",
    role = Role.ADMIN,
    status = UmStatus.ACTIVE,
    phone = "0812345678",
    email = "somchai@pharm.app",
    createdDate = null,
    updatedDate = null,
)

private val sampleProfileState = ProfileUiState(
    user = sampleProfileUser,
    form = ProfileFormFields(
        firstName = sampleProfileUser.firstName,
        lastName = sampleProfileUser.lastName,
        phone = sampleProfileUser.phone,
        email = sampleProfileUser.email,
    ),
)

@Preview
@Composable
private fun ProfileContent_Loaded_Preview() {
    PharmacyTheme {
        ProfileContent(state = sampleProfileState, callbacks = ProfileCallbacks())
    }
}

@Preview
@Composable
private fun ProfileContent_Loading_Preview() {
    PharmacyTheme {
        ProfileContent(state = ProfileUiState(loading = true), callbacks = ProfileCallbacks())
    }
}
