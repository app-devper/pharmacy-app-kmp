package app.devper.pharm.presentation.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.i18n.pharmStrings
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ActionDialog(
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val target = state.actionTarget ?: return
    val mode = state.actionMode ?: return
    val s = pharmStrings
    val title = when (mode) {
        UsersAction.Delete       -> s.usersConfirmDeleteTitle
        UsersAction.EditRole     -> s.usersConfirmRoleTitle
        UsersAction.ToggleStatus -> if (target.status.isActive) s.usersConfirmSuspendTitle else s.usersConfirmEnableTitle
        UsersAction.SetPassword  -> s.usersSetPasswordTitle(target.displayName)
    }
    PharmModal(
        open = true,
        onDismiss = callbacks.onDismissAction,
        title = title,
        dismissEnabled = !state.actionBusy,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            when (mode) {
                UsersAction.Delete       -> DeleteDialogBody(target = target, state = state, callbacks = callbacks)
                UsersAction.EditRole     -> RoleDialogBody(target = target, actorRole = state.currentUserRole, state = state, callbacks = callbacks)
                UsersAction.ToggleStatus -> StatusDialogBody(target = target, state = state, callbacks = callbacks)
                UsersAction.SetPassword  -> PasswordDialogBody(target = target, state = state, callbacks = callbacks)
            }
        }
    }
}

@Composable
private fun DeleteDialogBody(
    target: UmUser,
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val s = pharmStrings
    Text(text = s.usersConfirmDeleteMessage(target.username), style = PharmText.body)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PharmButton(
            label = s.commonCancel,
            onClick = callbacks.onDismissAction,
            variant = PharmButtonVariant.Ghost,
            enabled = !state.actionBusy,
        )
        PharmButton(
            label = s.commonDelete,
            onClick = callbacks.onConfirmDelete,
            variant = PharmButtonVariant.Danger,
            loading = state.actionBusy,
        )
    }
}

@Composable
private fun RoleDialogBody(
    target: UmUser,
    actorRole: Role,
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val s = pharmStrings
    Text(text = "@${target.username}", style = PharmText.micro.copy(color = pharmTokens.colors.fgMuted))
    val options = roleOptionsFor(actorRole)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { role ->
            PharmButton(
                label = role.label(s),
                onClick = { callbacks.onSubmitRoleChange(role) },
                variant = if (role == target.role) PharmButtonVariant.Primary else PharmButtonVariant.Outline,
                enabled = !state.actionBusy,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    PharmButton(
        label = s.commonCancel,
        onClick = callbacks.onDismissAction,
        variant = PharmButtonVariant.Ghost,
        enabled = !state.actionBusy,
    )
}

@Composable
private fun StatusDialogBody(
    target: UmUser,
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val s = pharmStrings
    val nextActive = !target.status.isActive
    Text(
        text = if (nextActive) s.usersConfirmEnableMessage(target.username) else s.usersConfirmSuspendMessage(target.username),
        style = PharmText.body,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PharmButton(
            label = s.commonCancel,
            onClick = callbacks.onDismissAction,
            variant = PharmButtonVariant.Ghost,
            enabled = !state.actionBusy,
        )
        PharmButton(
            label = if (nextActive) s.usersActionEnable else s.usersActionSuspend,
            onClick = callbacks.onConfirmStatusToggle,
            variant = PharmButtonVariant.Primary,
            loading = state.actionBusy,
        )
    }
}

@Composable
private fun PasswordDialogBody(
    target: UmUser,
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val s = pharmStrings
    var pwd by rememberSaveable(target.id) { mutableStateOf("") }
    var confirm by rememberSaveable(target.id) { mutableStateOf("") }
    var validationRequested by rememberSaveable(target.id) { mutableStateOf(false) }
    val passwordFocus = remember { FocusRequester() }
    val confirmFocus = remember { FocusRequester() }
    val matches = pwd.length >= 8 && pwd == confirm
    val passwordError = validationRequested && pwd.length < 8
    val confirmError = validationRequested && (confirm.isBlank() || pwd != confirm)
    FormField(
        label = s.usersFormPasswordNew,
        required = true,
        error = if (passwordError) s.usersFormPasswordHint else null,
    ) {
        PharmTextField(
            value = pwd,
            onValueChange = { pwd = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            isError = passwordError,
            focusRequester = passwordFocus,
        )
    }
    FormField(
        label = s.profilePasswordConfirm,
        required = true,
        error = if (confirmError) {
            if (confirm.isBlank()) s.validationRequired(s.profilePasswordConfirm) else s.profilePasswordMismatch
        } else {
            null
        },
    ) {
        PharmTextField(
            value = confirm,
            onValueChange = { confirm = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            isError = confirmError,
            focusRequester = confirmFocus,
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PharmButton(
            label = s.commonCancel,
            onClick = callbacks.onDismissAction,
            variant = PharmButtonVariant.Ghost,
            enabled = !state.actionBusy,
        )
        PharmButton(
            label = s.usersActionSetPassword,
            onClick = {
                if (matches) {
                    callbacks.onSubmitPasswordSet(pwd)
                } else {
                    validationRequested = true
                    if (pwd.length < 8) passwordFocus.requestFocus() else confirmFocus.requestFocus()
                }
            },
            variant = PharmButtonVariant.Primary,
            enabled = !state.actionBusy,
            loading = state.actionBusy,
        )
    }
}

internal fun roleOptionsFor(actor: Role): List<Role> = when (actor) {
    Role.SUPER -> listOf(Role.ADMIN, Role.MANAGER, Role.USER)
    Role.ADMIN -> listOf(Role.MANAGER, Role.USER)
    else       -> emptyList()
}
