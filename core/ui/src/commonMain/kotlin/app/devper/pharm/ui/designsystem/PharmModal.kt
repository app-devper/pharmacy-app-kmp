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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import app.devper.pharm.ui.common.pharmClickable

enum class PharmModalSize { Sm, Md, Lg, Xl }

@OptIn(ExperimentalLayoutApi::class)
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
    val modalMaxWidth: Dp = when (size) {
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
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            val compact = this.maxWidth < PharmBreakpoint.Medium
            val shape = if (compact) RectangleShape else t.shapes.lg
            val reducedMotion = LocalReducedMotion.current
            val enterProgress = remember { Animatable(if (reducedMotion) 1f else 0f) }
            val modalSizeModifier = if (compact) {
                Modifier.fillMaxSize()
            } else {
                Modifier.padding(16.dp).widthIn(max = modalMaxWidth).fillMaxWidth()
            }
            LaunchedEffect(reducedMotion) {
                if (reducedMotion) {
                    enterProgress.snapTo(1f)
                } else if (enterProgress.value < 1f) {
                    enterProgress.animateTo(1f, tween(PharmMotion.Medium))
                }
            }
            Column(
                modifier = modifier
                    .graphicsLayer {
                        alpha = enterProgress.value
                        val scale = if (compact) 1f else 0.92f + 0.08f * enterProgress.value
                        scaleX = scale
                        scaleY = scale
                    }
                    .then(modalSizeModifier)
                    .clip(shape)
                    .background(t.colors.surface, shape)
                    .border(1.dp, t.colors.borderSubtle, shape)
                    .then(if (compact) Modifier.windowInsetsPadding(WindowInsets.safeDrawing) else Modifier),
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
                        .weight(1f, fill = compact)
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
                    FlowRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(t.colors.bgPage)
                            .padding(horizontal = 20.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        itemVerticalAlignment = Alignment.CenterVertically,
                    ) {
                        footer()
                    }
                }
            }
        }
    }
}
