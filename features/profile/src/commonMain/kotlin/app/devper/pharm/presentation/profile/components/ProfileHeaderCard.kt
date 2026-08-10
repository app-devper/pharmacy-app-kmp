package app.devper.pharm.presentation.profile.components

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.Role
import app.devper.pharm.domain.model.UmUser
import app.devper.pharm.ui.designsystem.PharmBadge
import app.devper.pharm.ui.designsystem.RoleBadge
import app.devper.pharm.ui.designsystem.PharmBadgeTone
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun ProfileHeaderCard(user: UmUser) {
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
                RoleBadge(role = user.role)
                PharmBadge(
                    text = if (user.status.isActive) pharmStrings.commonStatusActive else pharmStrings.commonStatusInactive,
                    tone = if (user.status.isActive) PharmBadgeTone.Green else PharmBadgeTone.Gray,
                )
            }
        }
    }
}


