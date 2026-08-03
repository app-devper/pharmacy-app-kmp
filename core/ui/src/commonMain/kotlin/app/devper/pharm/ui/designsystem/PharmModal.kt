package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import app.devper.pharm.ui.components.PharmBreakpoint
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

enum class PharmModalSize { Sm, Md, Lg, Xl }

internal enum class PharmModalInitialFocusTarget { Custom, Close, None }
internal enum class PharmModalPresentation { FullScreen, Floating }

internal fun modalPresentation(windowWidth: Dp): PharmModalPresentation =
    if (windowWidth < PharmBreakpoint.Medium) PharmModalPresentation.FullScreen else PharmModalPresentation.Floating

internal fun modalWidth(size: PharmModalSize): Dp = when (size) {
    PharmModalSize.Sm -> 384.dp
    PharmModalSize.Md -> 448.dp
    PharmModalSize.Lg -> 672.dp
    PharmModalSize.Xl -> 896.dp
}

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
    initialFocusRequester: FocusRequester? = null,
    returnFocusRequester: FocusRequester? = null,
    fillHeight: Boolean = false,
    dialogMaxWidth: Dp? = null,
    dialogMaxHeight: Dp = 720.dp,
    contentScrollable: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(20.dp),
    footer: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    if (!open) return
    val t = pharmTokens
    val closeFocusRequester = remember { FocusRequester() }
    val currentReturnFocusRequester by rememberUpdatedState(returnFocusRequester)
    val initialFocusTarget = modalInitialFocusTarget(
        hasCustomRequester = initialFocusRequester != null,
        hasDismissibleClose = title != null && dismissEnabled,
    )
    LaunchedEffect(initialFocusTarget, initialFocusRequester) {
        val requester = when (initialFocusTarget) {
            PharmModalInitialFocusTarget.Custom -> initialFocusRequester
            PharmModalInitialFocusTarget.Close -> closeFocusRequester
            PharmModalInitialFocusTarget.None -> null
        }
        requester?.let { runCatching { it.requestFocus() } }
    }
    DisposableEffect(Unit) {
        onDispose {
            currentReturnFocusRequester?.let { runCatching { it.requestFocus() } }
        }
    }
    val modalMaxWidth = dialogMaxWidth ?: modalWidth(size)
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
            val presentation = modalPresentation(this.maxWidth)
            val compact = presentation == PharmModalPresentation.FullScreen
            val shape = if (compact) RectangleShape else t.shapes.xl
            val reducedMotion = LocalReducedMotion.current
            val enterProgress = remember { Animatable(if (reducedMotion) 1f else 0f) }
            val modalSizeModifier = if (compact) {
                Modifier.fillMaxSize()
            } else if (fillHeight) {
                Modifier
                    .padding(16.dp)
                    .widthIn(max = modalMaxWidth)
                    .fillMaxWidth()
                    .heightIn(max = dialogMaxHeight)
                    .fillMaxHeight()
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
                        val scale = if (compact) 1f else 0.96f + 0.04f * enterProgress.value
                        scaleX = scale
                        scaleY = scale
                    }
                    .then(modalSizeModifier)
                    .clip(shape)
                    .background(t.colors.surface, shape)
                    .border(1.dp, t.colors.borderSubtle, shape)
                    .then(if (compact) Modifier.windowInsetsPadding(WindowInsets.safeDrawing) else Modifier)
                    .semantics { title?.let { paneTitle = it } },
            ) {
                if (title != null) {
                    val closeDesc = pharmStrings.commonClose
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, top = 18.dp, end = 68.dp, bottom = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp),
                        ) {
                            Text(
                                text = title,
                                style = PharmText.h2,
                                modifier = Modifier.semantics { heading() },
                            )
                            if (subtitle != null) {
                                Text(subtitle, style = PharmText.meta)
                            }
                        }
                        PharmIconButton(
                            contentDescription = closeDesc,
                            onClick = onDismiss,
                            enabled = dismissEnabled,
                            minSize = t.dimens.minimumTouchTarget,
                            shape = t.shapes.pill,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(top = 8.dp, end = 8.dp)
                                .size(t.dimens.minimumTouchTarget)
                                .focusRequester(closeFocusRequester),
                        ) {
                            Icon(
                                imageVector = PharmIcons.Close,
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

                val contentModifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = compact || fillHeight)
                    .then(
                        if (contentScrollable) {
                            Modifier.verticalScroll(rememberScrollState())
                        } else {
                            Modifier
                        },
                    )
                    .padding(contentPadding)
                Column(modifier = contentModifier) {
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

internal fun modalInitialFocusTarget(
    hasCustomRequester: Boolean,
    hasDismissibleClose: Boolean,
): PharmModalInitialFocusTarget = when {
    hasCustomRequester -> PharmModalInitialFocusTarget.Custom
    hasDismissibleClose -> PharmModalInitialFocusTarget.Close
    else -> PharmModalInitialFocusTarget.None
}
