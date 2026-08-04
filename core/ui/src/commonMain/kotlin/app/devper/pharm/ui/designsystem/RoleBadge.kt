package app.devper.pharm.ui.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings

fun Role.tone(): PharmBadgeTone = when (this) {
    Role.SUPER -> PharmBadgeTone.Purple
    Role.ADMIN -> PharmBadgeTone.Blue
    Role.MANAGER -> PharmBadgeTone.Indigo
    Role.USER -> PharmBadgeTone.Gray
    Role.UNKNOWN -> PharmBadgeTone.Gray
}

fun Role.label(s: PharmStrings): String = when (this) {
    Role.SUPER -> s.usersRoleSuper
    Role.ADMIN -> s.usersRoleAdmin
    Role.MANAGER -> s.usersRoleManager
    Role.USER -> s.usersRoleUser
    Role.UNKNOWN -> s.usersRoleUnknown
}

@Composable
fun RoleBadge(
    role: Role,
    modifier: Modifier = Modifier,
    size: PharmBadgeSize = PharmBadgeSize.Md,
) {
    PharmBadge(
        text = role.label(pharmStrings),
        tone = role.tone(),
        size = size,
        modifier = modifier,
    )
}
