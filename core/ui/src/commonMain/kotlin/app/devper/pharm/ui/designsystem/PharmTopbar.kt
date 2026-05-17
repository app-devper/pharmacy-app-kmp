package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

data class TopbarUser(
    val initial: String,
    val name: String,
    val role: String,
)

@Composable
fun PharmTopbar(
    title: String,
    modifier: Modifier = Modifier,
    user: TopbarUser? = null,
    online: Boolean = true,
    showHamburger: Boolean = false,
    onHamburger: () -> Unit = {},
    onLogout: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val t = pharmTokens
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(t.dimens.topbarHeight)
            .background(t.colors.surface)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (showHamburger) {
            HamburgerButton(onClick = onHamburger)
        }
        Text(text = title, style = PharmText.h1)
        Box(modifier = Modifier.weight(1f))
        if (online) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(t.shapes.pill)
                        .background(Color(0xFF22C55E)),
                )
                Text("ออนไลน์", style = PharmText.meta)
            }
        }
        if (user != null) {

            Box(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .width(1.dp)
                    .fillMaxHeight()
                    .padding(vertical = 12.dp)
                    .background(t.colors.border),
            )
            UserChip(user = user, onLogout = onLogout, onProfileClick = onProfileClick)
        }
        trailing?.invoke()
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(t.colors.border),
    )
}

@Composable
private fun HamburgerButton(onClick: () -> Unit) {
    val t = pharmTokens
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(t.shapes.sm)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = PharmIcons.Hamburger,
            contentDescription = "เมนู",
            tint = t.colors.fg2,
            modifier = Modifier.size(20.dp),
        )
    }
}

@Composable
private fun UserChip(user: TopbarUser, onLogout: (() -> Unit)?, onProfileClick: (() -> Unit)?) {
    val t = pharmTokens
    Row(
        modifier = Modifier.padding(start = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(t.shapes.pill)
                .background(t.colors.accentBgSoft),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = user.initial,
                style = PharmText.body.copy(
                    color = t.colors.accent,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                ),
            )
        }
        val nameModifier = if (onProfileClick != null) {
            Modifier
                .clip(t.shapes.sm)
                .clickable(onClick = onProfileClick)
                .padding(horizontal = 4.dp, vertical = 2.dp)
        } else {
            Modifier
        }
        Column(
            modifier = nameModifier,
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            Text(
                text = user.name,
                style = PharmText.body.copy(
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                ),
            )
            Text(text = user.role, style = PharmText.micro)
        }
        if (onLogout != null) {
            Box(
                modifier = Modifier
                    .clip(t.shapes.sm)
                    .clickable(onClick = onLogout)
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) {
                Text(text = "ออก", style = PharmText.meta)
            }
        }
    }
}
