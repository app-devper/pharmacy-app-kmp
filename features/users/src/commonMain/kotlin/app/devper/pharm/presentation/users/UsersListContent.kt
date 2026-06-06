package app.devper.pharm.presentation.users

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmStatus
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.util.UmRoleValidator
import app.devper.pharm.ui.components.ErrorBottomSheet
import app.devper.pharm.ui.designsystem.FormField
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmListResultLine
import app.devper.pharm.ui.designsystem.PharmListSkeleton
import app.devper.pharm.ui.designsystem.PharmListToolbar
import app.devper.pharm.ui.designsystem.PharmTextField
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.pharmTokens
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun UsersListContent(
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val t = pharmTokens
    val visible = state.filtered
    val searching = state.searchQuery.isNotBlank()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.bgPage)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg),
        ) {
            UsersListToolbar(state = state, callbacks = callbacks)
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))
            PharmListResultLine(
                total = state.users.size,
                noun = "คน",
                visible = visible.size,
                searching = searching,
            )
            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(t.colors.divider))

            when {
                state.loading && state.users.isEmpty() -> PharmListSkeleton()
                state.users.isEmpty() && state.searchQuery.isBlank() -> PharmEmptyState(
                    icon = PharmIcons.Users,
                    title = "ยังไม่มีผู้ใช้งาน",
                    action = {
                        if (UmRoleValidator.canManageUsers(state.currentUserRole)) {
                            PharmButton(
                                label = "เพิ่มผู้ใช้งานคนแรก",
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
    }

    ActionDialog(state = state, callbacks = callbacks)
    ErrorBottomSheet(message = state.error, onDismiss = callbacks.onDismissError)
}

@Composable
private fun UsersListToolbar(
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    PharmListToolbar(
        title = "จัดการผู้ใช้งาน",
        subtitle = "บัญชีผู้ใช้ในระบบ User Management",
        searchValue = state.searchQuery,
        onSearchChange = callbacks.onSearch,
        searchPlaceholder = "ค้นหาชื่อ / username / อีเมล…",
        titleStyle = PharmText.h2,
        actions = {
            if (UmRoleValidator.canManageUsers(state.currentUserRole)) {
                PharmButton(
                    label = "เพิ่มผู้ใช้งาน",
                    onClick = callbacks.onAddUser,
                    size = PharmButtonSize.Sm,
                    leadingIcon = { Icon(PharmIcons.Plus, contentDescription = null) },
                )
            }
        },
    )
}

@Composable
private fun ActionDialog(
    state: UsersListUiState,
    callbacks: UsersListCallbacks,
) {
    val target = state.actionTarget ?: return
    val mode = state.actionMode ?: return
    val t = pharmTokens
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(t.colors.scrim)
            .clickable(enabled = !state.actionBusy, onClick = callbacks.onDismissAction),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 480.dp)
                .clip(t.shapes.lg)
                .background(t.colors.surface)
                .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
                .clickable(enabled = false, onClick = {})
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
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
    Text(text = "ยืนยันลบผู้ใช้งาน", style = PharmText.h2)
    Text(
        text = "ลบผู้ใช้งาน \"${target.username}\" ?\nการดำเนินการนี้ไม่สามารถกู้คืนได้",
        style = PharmText.body,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PharmButton(
            label = "ยกเลิก",
            onClick = callbacks.onDismissAction,
            variant = PharmButtonVariant.Ghost,
            enabled = !state.actionBusy,
        )
        PharmButton(
            label = "ลบ",
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
    Text(text = "เปลี่ยน Role", style = PharmText.h2)
    Text(text = "@${target.username}", style = PharmText.micro.copy(color = pharmTokens.colors.fgMuted))
    val options = roleOptionsFor(actorRole)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { role ->
            PharmButton(
                label = role.label(),
                onClick = { callbacks.onSubmitRoleChange(role) },
                variant = if (role == target.role) PharmButtonVariant.Primary else PharmButtonVariant.Outline,
                enabled = !state.actionBusy,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
    PharmButton(
        label = "ยกเลิก",
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
    val nextActive = !target.status.isActive
    Text(
        text = if (nextActive) "ยืนยันเปิดใช้งาน" else "ยืนยันระงับการใช้งาน",
        style = PharmText.h2,
    )
    Text(
        text = "${if (nextActive) "เปิดใช้งาน" else "ระงับ"}ผู้ใช้ \"${target.username}\"",
        style = PharmText.body,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PharmButton(
            label = "ยกเลิก",
            onClick = callbacks.onDismissAction,
            variant = PharmButtonVariant.Ghost,
            enabled = !state.actionBusy,
        )
        PharmButton(
            label = if (nextActive) "เปิดใช้" else "ระงับ",
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
    var pwd by rememberSaveable(target.id) { mutableStateOf("") }
    var confirm by rememberSaveable(target.id) { mutableStateOf("") }
    val matches = pwd.length >= 8 && pwd == confirm
    Text(text = "ตั้งรหัสผ่าน — ${target.displayName}", style = PharmText.h2)
    FormField(label = "รหัสผ่านใหม่ (≥8 ตัว)", required = true) {
        PharmTextField(
            value = pwd,
            onValueChange = { pwd = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
        )
    }
    val confirmError = confirm.isNotBlank() && pwd != confirm
    FormField(
        label = "ยืนยันรหัสผ่าน",
        required = true,
        error = if (confirmError) "ไม่ตรงกับรหัสผ่าน" else null,
    ) {
        PharmTextField(
            value = confirm,
            onValueChange = { confirm = it },
            visualTransformation = PasswordVisualTransformation(),
            keyboardType = KeyboardType.Password,
            isError = confirmError,
        )
    }
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        PharmButton(
            label = "ยกเลิก",
            onClick = callbacks.onDismissAction,
            variant = PharmButtonVariant.Ghost,
            enabled = !state.actionBusy,
        )
        PharmButton(
            label = "ตั้งรหัสผ่าน",
            onClick = { callbacks.onSubmitPasswordSet(pwd) },
            variant = PharmButtonVariant.Primary,
            enabled = matches,
            loading = state.actionBusy,
        )
    }
}

private fun roleOptionsFor(actor: Role): List<Role> = when (actor) {
    Role.SUPER -> listOf(Role.ADMIN, Role.MANAGER, Role.USER)
    Role.ADMIN -> listOf(Role.MANAGER, Role.USER)
    else       -> emptyList()
}

private val previewUsers = listOf(
    UmUser(
        id = "u-1", firstName = "สมชาย", lastName = "ใจดี", username = "somchai",
        clientId = "PHA", role = Role.SUPER, status = UmStatus.ACTIVE,
        phone = "0812345678", email = "somchai@example.com",
        createdDate = "", updatedDate = "",
    ),
    UmUser(
        id = "u-2", firstName = "สมหญิง", lastName = "พริ้งพราย", username = "somying",
        clientId = "PHA", role = Role.ADMIN, status = UmStatus.ACTIVE,
        phone = "0898765432", email = "somying@example.com",
        createdDate = "", updatedDate = "",
    ),
    UmUser(
        id = "u-3", firstName = "ดวงดี", lastName = "มีสุข", username = "duangdee",
        clientId = "PHA", role = Role.USER, status = UmStatus.INACTIVE,
        phone = "", email = "",
        createdDate = "", updatedDate = "",
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
