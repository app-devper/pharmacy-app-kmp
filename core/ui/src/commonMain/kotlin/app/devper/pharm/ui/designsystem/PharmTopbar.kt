package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.LocalThemeController
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.common.pharmClickable

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
    showThemeToggle: Boolean = true,
    showStatus: Boolean = true,
    compactUserMenu: Boolean = false,
    onBack: (() -> Unit)? = null,
    actions: (@Composable () -> Unit)? = null,
    onHamburger: () -> Unit = {},
    onLogout: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val t = pharmTokens
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val horizontalPadding = if (maxWidth < 360.dp) 8.dp else 16.dp
        val itemSpacing = if (maxWidth < 360.dp) 6.dp else 12.dp
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(t.dimens.topbarHeight)
                .background(t.colors.surface)
                .padding(horizontal = horizontalPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            if (onBack != null) {
                BackButton(onClick = onBack)
            } else if (showHamburger) {
                HamburgerButton(onClick = onHamburger)
            }
            Text(
                text = title,
                style = PharmText.h1,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            actions?.invoke()
            if (onBack == null && showThemeToggle) {
                ThemeToggleButton()
            }
            if (onBack == null && showStatus && online) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(t.shapes.pill)
                            .background(t.colors.successFg),
                    )
                    Text(pharmStrings.commonOnline, style = PharmText.meta)
                }
            }
            if (onBack == null && user != null) {
                if (compactUserMenu) {
                    PharmActionMenu(
                        actions = buildList {
                            if (onProfileClick != null) {
                                add(
                                    PharmAction(
                                        label = pharmStrings.profileTitle,
                                        icon = PharmIcons.Person,
                                        onClick = onProfileClick,
                                    ),
                                )
                            }
                            if (onLogout != null) {
                                add(
                                    PharmAction(
                                        label = pharmStrings.commonLogout,
                                        icon = PharmIcons.Logout,
                                        tone = PharmActionTone.Danger,
                                        onClick = onLogout,
                                    ),
                                )
                            }
                        }
                    )
                } else {
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
            }
            trailing?.invoke()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(t.colors.border),
    )
}

@Composable
private fun ThemeToggleButton() {
    val t = pharmTokens
    val controller = LocalThemeController.current
    if (!controller.canToggle) return
    val description = if (controller.isDark) {
        pharmStrings.commonSwitchToLightTheme
    } else {
        pharmStrings.commonSwitchToDarkTheme
    }
    PharmIconButton(
        contentDescription = description,
        onClick = controller.toggle,
        minSize = t.dimens.controlHeight,
        shape = t.shapes.sm,
        modifier = Modifier
            .size(t.dimens.controlHeight),
    ) {
        Icon(
            imageVector = if (controller.isDark) PharmIcons.Sun else PharmIcons.Moon,
            contentDescription = null,
            tint = t.colors.fg2,
            modifier = Modifier.size(18.dp),
        )
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    val t = pharmTokens
    PharmIconButton(
        contentDescription = pharmStrings.commonBack,
        onClick = onClick,
        minSize = t.dimens.controlHeight,
        modifier = Modifier
            .size(t.dimens.controlHeight),
    ) {
        Icon(
            imageVector = PharmIcons.ChevronLeft,
            contentDescription = null,
            tint = t.colors.fg1,
            modifier = Modifier.size(22.dp),
        )
    }
}

@Composable
private fun HamburgerButton(onClick: () -> Unit) {
    val t = pharmTokens
    PharmIconButton(
        contentDescription = pharmStrings.commonMenu,
        onClick = onClick,
        minSize = t.dimens.controlHeight,
        shape = t.shapes.sm,
        modifier = Modifier
            .sizeIn(minWidth = t.dimens.controlHeight, minHeight = t.dimens.controlHeight),
    ) {
        Icon(
            imageVector = PharmIcons.Hamburger,
            contentDescription = null,
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
                .heightIn(min = t.dimens.controlHeight)
                .clip(t.shapes.sm)
                .pharmClickable(role = Role.Button, shape = t.shapes.sm, onClick = onProfileClick)
                .padding(horizontal = 6.dp, vertical = 4.dp)
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
            PharmIconButton(
                contentDescription = pharmStrings.commonLogout,
                onClick = onLogout,
                minSize = t.dimens.controlHeight,
                shape = t.shapes.sm,
                modifier = Modifier
                    .sizeIn(minWidth = t.dimens.controlHeight, minHeight = t.dimens.controlHeight),
            ) {
                Icon(
                    imageVector = PharmIcons.Logout,
                    contentDescription = null,
                    tint = t.colors.dangerFg,
                    modifier = Modifier.size(16.dp),
                )
            }
        }
    }
}
