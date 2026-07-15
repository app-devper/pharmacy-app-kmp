package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

enum class PharmModalSize { Sm, Md, Lg, Xl }

@Composable
fun PharmModal(
    open: Boolean,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    size: PharmModalSize = PharmModalSize.Md,
    dismissEnabled: Boolean = true,
    dismissOnBackPress: Boolean = true,
    dismissOnClickOutside: Boolean = true,
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (!open) return
    val t = pharmTokens
    val maxWidth: Dp = when (size) {
        PharmModalSize.Sm -> 384.dp
        PharmModalSize.Md -> 448.dp
        PharmModalSize.Lg -> 672.dp
        PharmModalSize.Xl -> 896.dp
    }
    Dialog(
        onDismissRequest = { if (dismissEnabled) onDismiss() },
        properties = DialogProperties(
            dismissOnBackPress = dismissEnabled && dismissOnBackPress,
            dismissOnClickOutside = dismissEnabled && dismissOnClickOutside,
            usePlatformDefaultWidth = false,
        ),
    ) {
        val shape = t.shapes.lg
        val enterProgress = remember { Animatable(0f) }
        LaunchedEffect(Unit) { enterProgress.animateTo(1f, tween(PharmMotion.Medium)) }
        Column(
            modifier = modifier
                .graphicsLayer {
                    alpha = enterProgress.value
                    val scale = 0.92f + 0.08f * enterProgress.value
                    scaleX = scale
                    scaleY = scale
                }
                .widthIn(max = maxWidth)
                .fillMaxWidth()
                .padding(16.dp)
                .clip(shape)
                .background(t.colors.surface, shape)
                .border(1.dp, t.colors.borderSubtle, shape),
        ) {
            if (title != null) {
                val closeDesc = pharmStrings.commonClose
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 16.dp, end = 68.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp),
                    ) {
                        Text(title, style = PharmText.h2)
                        if (subtitle != null) {
                            Text(subtitle, style = PharmText.meta)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .size(t.dimens.controlHeight)
                            .clip(CircleShape)
                            .border(1.dp, t.colors.border, CircleShape)
                            .pharmClickable(
                                enabled = dismissEnabled,
                                role = Role.Button,
                                shape = CircleShape,
                                onClick = onDismiss,
                            )
                            .semantics(mergeDescendants = true) {
                                contentDescription = closeDesc
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = t.colors.fg2,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(t.colors.divider),
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
            ) {
                content()
            }

            if (footer != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(t.colors.divider),
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(t.colors.bgPage)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    footer()
                }
            }
        }
    }
}
