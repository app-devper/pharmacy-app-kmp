package app.devper.pharm.presentation.users

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.domain.extension.canManage
import app.devper.pharm.domain.extension.canManageUsers
import app.devper.pharm.domain.extension.canViewUsers
import app.devper.pharm.ui.designsystem.PharmAction
import app.devper.pharm.ui.designsystem.PharmActionMenu
import app.devper.pharm.ui.designsystem.PharmActionTone
import app.devper.pharm.ui.designsystem.PharmAvatarCircle
import app.devper.pharm.ui.designsystem.PharmAvatarSize
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.PharmBadgeSize
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.designsystem.PharmColumnAlign
import app.devper.pharm.ui.designsystem.PharmEmptyState
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.designsystem.PharmStatus
import app.devper.pharm.ui.designsystem.PharmStatusBadge
import app.devper.pharm.ui.designsystem.PharmTable
import app.devper.pharm.ui.designsystem.PharmTableColumn
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun UsersListTable(
    users: List<UmUser>,
    actorRole: Role,
    currentUserId: String?,
    callbacks: UsersListCallbacks,
    modifier: Modifier = Modifier,
    emptySearching: Boolean = false,
) {
    val columns = remember(callbacks, currentUserId, actorRole) {
        listOf(
        PharmTableColumn<UmUser>(
            header = "ชื่อ-นามสกุล",
            weight = 2.2f,
            cell = { user -> UserNameCell(user = user, isSelf = user.id == currentUserId) },
        ),
        PharmTableColumn(
            header = "Username",
            weight = 1.2f,
            cell = { user -> UserUsernameCell(user) },
        ),
        PharmTableColumn(
            header = "เบอร์โทร",
            weight = 1.0f,
            cell = { user -> UserPhoneCell(user) },
        ),
        PharmTableColumn(
            header = "Role",
            weight = 0.9f,
            cell = { user ->
                PharmBadge(
                    text = user.role.label(),
                    tone = user.role.tone(),
                    size = PharmBadgeSize.Sm,
                )
            },
        ),
        PharmTableColumn(
            header = "สถานะ",
            weight = 0.8f,
            cell = { user ->
                PharmStatusBadge(
                    status = if (user.status.isActive) PharmStatus.Active else PharmStatus.Inactive,
                    size = PharmBadgeSize.Sm,
                )
            },
        ),
        PharmTableColumn(
            header = "จัดการ",
            weight = 0.6f,
            align = PharmColumnAlign.End,
            cell = { user ->
                UsersRowActions(
                    user = user,
                    actorRole = actorRole,
                    isSelf = user.id == currentUserId,
                    callbacks = callbacks,
                )
            },
        ),
        )
    }

    PharmTable(
        rows = users,
        columns = columns,
        key = { it.id },
        modifier = modifier,
        onRowClick = { callbacks.onEditUser(it) },
        rowHeight = 60.dp,
        emptyContent = {
            if (emptySearching) {
                PharmEmptyState(
                    icon = PharmIcons.Search,
                    title = "ไม่พบผู้ใช้งานที่ค้นหา",
                )
            } else {
                PharmEmptyState(
                    icon = PharmIcons.Users,
                    title = "ยังไม่มีผู้ใช้งาน",
                )
            }
        },
    )
}

@Composable
private fun UserNameCell(user: UmUser, isSelf: Boolean) {
    val t = pharmTokens
    androidx.compose.foundation.layout.Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PharmAvatarCircle(
            text = user.initials,
            size = PharmAvatarSize.Md,
            tone = user.role.tone(),
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(
                text = user.displayName,
                style = PharmText.bodySm.copy(fontWeight = FontWeight.Medium),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (isSelf) {
                Text(
                    text = "บัญชีของคุณ",
                    style = PharmText.micro.copy(color = t.colors.accent),
                )
            }
        }
    }
}

@Composable
private fun UserUsernameCell(user: UmUser) {
    val t = pharmTokens
    Text(
        text = user.username,
        style = PharmText.micro.copy(
            color = t.colors.fg2,
            fontFamily = FontFamily.Monospace,
        ),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun UserPhoneCell(user: UmUser) {
    val t = pharmTokens
    val phone = user.phone.takeIf { it.isNotBlank() }
    if (phone == null) {
        Text(text = "—", style = PharmText.meta.copy(color = t.colors.fgMuted))
    } else {
        Text(
            text = phone,
            style = PharmText.bodySm.copy(color = t.colors.fg2),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun UsersRowActions(
    user: UmUser,
    actorRole: Role,
    isSelf: Boolean,
    callbacks: UsersListCallbacks,
) {
    val canManage = actorRole.canManage(target = user.role, isSelf = isSelf)
    val actions = buildList {
        add(
            PharmAction(
                label = "แก้ไข",
                icon = PharmIcons.Pencil,
                tone = PharmActionTone.Primary,
                onClick = { callbacks.onEditUser(user) },
            ),
        )
        if (canManage) {
            add(
                PharmAction(
                    label = "เปลี่ยน Role",
                    icon = PharmIcons.Person,
                    onClick = { callbacks.onRequestRoleEdit(user) },
                ),
            )
            add(
                PharmAction(
                    label = if (user.status.isActive) "ระงับ" else "เปิดใช้",
                    icon = PharmIcons.Ban,
                    onClick = { callbacks.onRequestStatusToggle(user) },
                ),
            )
            add(
                PharmAction(
                    label = "ตั้งรหัสผ่าน",
                    icon = PharmIcons.Pencil,
                    tone = PharmActionTone.Success,
                    onClick = { callbacks.onRequestPasswordSet(user) },
                ),
            )
            add(
                PharmAction(
                    label = "ลบ",
                    icon = PharmIcons.Trash,
                    tone = PharmActionTone.Danger,
                    onClick = { callbacks.onRequestDelete(user) },
                ),
            )
        }
    }
    PharmActionMenu(actions = actions)
}

internal fun Role.label(): String = when (this) {
    Role.SUPER   -> "Super Admin"
    Role.ADMIN   -> "Admin"
    Role.MANAGER -> "Manager"
    Role.USER    -> "User"
    Role.UNKNOWN -> "-"
}

internal fun Role.tone(): PharmBadgeTone = when (this) {
    Role.SUPER   -> PharmBadgeTone.Purple
    Role.ADMIN   -> PharmBadgeTone.Blue
    Role.MANAGER -> PharmBadgeTone.Indigo
    Role.USER    -> PharmBadgeTone.Gray
    Role.UNKNOWN -> PharmBadgeTone.Gray
}
