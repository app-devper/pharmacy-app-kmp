package app.devper.pharm.presentation.users

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.extension.canManageUsers
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.presentation.users.i18n.localizeUsers
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.components.unlessPageShowsError
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmErrorState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListScaffold
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme

@Composable
fun UsersListContent(
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val pageIsEmpty = state.users.isEmpty()
    val s = pharmStrings
    val visible = state.filtered
    val searching = state.searchQuery.isNotBlank()

    PharmListScaffold(
        toolbar = { UsersListToolbar(state = state, callbacks = callbacks) },
        resultLine = {
            PharmListResultLine(
                total = state.users.size,
                noun = s.usersCountNoun,
                visible = visible.size,
                searching = searching,
            )
        },
    ) {
        when {
            state.loading && pageIsEmpty -> PharmListSkeleton()
            state.errorState != null && pageIsEmpty -> PharmErrorState()
            pageIsEmpty && state.searchQuery.isBlank() -> PharmEmptyState(
                icon = PharmIcons.Users,
                title = s.usersListEmpty,
                action = {
                    if (state.currentUserRole.canManageUsers()) {
                        PharmButton(
                            label = s.usersAddFirstCta,
                            onClick = callbacks.onAddUser,
                            variant = PharmButtonVariant.Primary,
                            size = PharmButtonSize.Sm,
                            leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
                        )
                    }
                },
            )
            else -> UsersListTable(
                users = visible,
                actorRole = state.currentUserRole,
                currentUserId = state.currentUserId,
                callbacks = callbacks,
                emptySearching = searching,
            )
        }
    }

    ActionDialog(state = state, callbacks = callbacks)
    ErrorBottomSheet(message = state.errorState.unlessPageShowsError(pageIsEmpty)?.localizeUsers(pharmStrings), onDismiss = callbacks.onDismissError)
}

@Composable
private fun UsersListToolbar(
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val s = pharmStrings
    PharmListToolbar(
        subtitle = s.usersSubtitle,
        searchValue = state.searchQuery,
        onSearchChange = callbacks.onSearch,
        searchPlaceholder = s.usersSearchPlaceholder,
        compactTopbarActions = true,
        actions = {
            if (state.currentUserRole.canManageUsers()) {
                PharmButton(
                    label = s.usersAddCta,
                    onClick = callbacks.onAddUser,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
                )
            }
        },
    )
}

private val previewUsers = listOf(
    UmUser(
        id = "u-1", firstName = "สมชาย", lastName = "ใจดี", username = "somchai",
        clientId = "PHA", role = Role.SUPER, status = UmStatus.ACTIVE,
        phone = "0812345678", email = "somchai@example.com",
        createdDate = null, updatedDate = null,
    ),
    UmUser(
        id = "u-2", firstName = "สมหญิง", lastName = "พริ้งพราย", username = "somying",
        clientId = "PHA", role = Role.ADMIN, status = UmStatus.ACTIVE,
        phone = "0898765432", email = "somying@example.com",
        createdDate = null, updatedDate = null,
    ),
    UmUser(
        id = "u-3", firstName = "ดวงดี", lastName = "มีสุข", username = "duangdee",
        clientId = "PHA", role = Role.USER, status = UmStatus.INACTIVE,
        phone = "", email = "",
        createdDate = null, updatedDate = null,
    ),
)

@Preview
@Composable
private fun UsersListContent_Loaded_Preview() {
    PharmacyTheme {
        UsersListContent(
            state = UsersListUiState(
                users = previewUsers,
                currentUserId = "u-1",
                currentUserRole = Role.SUPER,
            ),
            callbacks = UsersListCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun UsersListContent_Empty_Preview() {
    PharmacyTheme {
        UsersListContent(
            state = UsersListUiState(currentUserRole = Role.ADMIN),
            callbacks = UsersListCallbacks.Preview,
        )
    }
}

@Preview
@Composable
private fun UsersListContent_Loading_Preview() {
    PharmacyTheme {
        UsersListContent(
            state = UsersListUiState(loading = true),
            callbacks = UsersListCallbacks.Preview,
        )
    }
}
