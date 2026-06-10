package app.devper.pharm.ui.common

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class ToastAction(
    val label: String,
    val onClick: () -> Unit,
)

sealed class PharmToast {
    abstract val message: String
    abstract val action: ToastAction?
    abstract val durationMs: Long

    data class Success(
        override val message: String,
        override val action: ToastAction? = null,
        override val durationMs: Long = 3000L,
    ) : PharmToast()

    data class Info(
        override val message: String,
        override val action: ToastAction? = null,
        override val durationMs: Long = 3000L,
    ) : PharmToast()

    data class Warning(
        override val message: String,
        override val action: ToastAction? = null,
        override val durationMs: Long = 4000L,
    ) : PharmToast()

    data class Error(
        override val message: String,
        override val action: ToastAction? = null,
        override val durationMs: Long = 5000L,
    ) : PharmToast()
}

open class PharmSnackbarHost {
    private val _events = MutableSharedFlow<PharmToast>(replay = 0, extraBufferCapacity = 4)
    val events: SharedFlow<PharmToast> = _events.asSharedFlow()

    open fun showToast(toast: PharmToast) {
        _events.tryEmit(toast)
    }

    companion object {
        val Noop: PharmSnackbarHost = object : PharmSnackbarHost() {
            override fun showToast(toast: PharmToast) = Unit
        }
    }
}

val LocalPharmSnackbar = staticCompositionLocalOf { PharmSnackbarHost.Noop }

@Composable
fun PharmSnackbarHostUi(
    host: PharmSnackbarHost,
    modifier: Modifier = Modifier,
) {
    var current by remember(host) { mutableStateOf<PharmToast?>(null) }

    LaunchedEffect(host) {
        host.events.collect { toast ->
            current = toast
            delay(toast.durationMs)
            current = null
        }
    }

    Box(
        modifier = modifier.fillMaxSize().padding(24.dp),
        contentAlignment = Alignment.BottomCenter,
    ) {
        AnimatedVisibility(
            visible = current != null,
            enter = fadeIn() + slideInVertically { it / 2 },
            exit = fadeOut() + slideOutVertically { it / 2 },
        ) {
            current?.let { toast ->
                ToastCard(
                    toast = toast,
                    onAction = {
                        toast.action?.onClick?.invoke()
                        current = null
                    },
                    onClose = { current = null },
                )
            }
        }
    }
}

private data class ToastColors(
    val bg: Color,
    val fg: Color,
    val icon: ImageVector,
)

@Composable
private fun toastColors(toast: PharmToast): ToastColors {
    val c = pharmTokens.colors
    return when (toast) {
        is PharmToast.Success -> ToastColors(c.successBg, c.successFg, PharmIcons.Check)
        is PharmToast.Info    -> ToastColors(c.infoBg,    c.infoFg,    PharmIcons.Info)
        is PharmToast.Warning -> ToastColors(c.warningBg, c.warningFg, PharmIcons.Warning)
        is PharmToast.Error   -> ToastColors(c.dangerBg,  c.dangerFg,  PharmIcons.Warning)
    }
}

@Composable
private fun ToastCard(
    toast: PharmToast,
    onAction: () -> Unit,
    onClose: () -> Unit,
) {
    val t = pharmTokens
    val colors = toastColors(toast)
    Row(
        modifier = Modifier
            .widthIn(min = 240.dp, max = 480.dp)
            .clip(t.shapes.lg)
            .background(colors.bg)
            .border(1.dp, colors.fg.copy(alpha = 0.35f), t.shapes.lg)
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .semantics(mergeDescendants = true) {
                liveRegion = when (toast) {
                    is PharmToast.Error, is PharmToast.Warning -> LiveRegionMode.Assertive
                    else -> LiveRegionMode.Polite
                }
            },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(
            imageVector = colors.icon,
            contentDescription = null,
            tint = colors.fg,
            modifier = Modifier.size(20.dp),
        )
        Text(
            text = toast.message,
            style = PharmText.bodySm.copy(color = colors.fg),
            modifier = Modifier.weight(1f),
        )
        toast.action?.let { action ->
            Box(
                modifier = Modifier
                    .heightIn(min = 48.dp)
                    .clip(t.shapes.sm)
                    .clickable(role = Role.Button, onClick = onAction)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = action.label,
                    style = PharmText.buttonSm.copy(color = colors.fg),
                )
            }
        }
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(t.shapes.pill)
                .clickable(role = Role.Button, onClick = onClose),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = PharmIcons.Close,
                contentDescription = pharmStrings.commonClose,
                tint = colors.fg.copy(alpha = 0.7f),
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
