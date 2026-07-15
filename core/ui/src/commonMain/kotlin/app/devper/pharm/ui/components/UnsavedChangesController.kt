package app.devper.pharm.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmModal
import app.devper.pharm.ui.designsystem.PharmModalSize
import app.devper.pharm.ui.i18n.pharmStrings

@Stable
class UnsavedChangesController {
    var hasUnsavedChanges: Boolean by mutableStateOf(false)
        private set
    var dialogOpen: Boolean by mutableStateOf(false)
        private set

    private var registrationToken: Any? = null
    private var pendingAction: (() -> Unit)? = null

    fun register(token: Any, dirty: Boolean) {
        registrationToken = token
        hasUnsavedChanges = dirty
    }

    fun unregister(token: Any) {
        if (registrationToken === token) {
            registrationToken = null
            hasUnsavedChanges = false
        }
    }

    fun request(action: () -> Unit) {
        if (hasUnsavedChanges) {
            pendingAction = action
            dialogOpen = true
        } else {
            action()
        }
    }

    fun keepEditing() {
        pendingAction = null
        dialogOpen = false
    }

    fun discardChanges() {
        val action = pendingAction
        pendingAction = null
        hasUnsavedChanges = false
        dialogOpen = false
        action?.invoke()
    }
}

val LocalUnsavedChangesController = staticCompositionLocalOf<UnsavedChangesController?> { null }

@Composable
fun RegisterUnsavedChanges(hasUnsavedChanges: Boolean) {
    val controller = LocalUnsavedChangesController.current ?: return
    val token = remember { Any() }
    SideEffect {
        controller.register(token, hasUnsavedChanges)
    }
    DisposableEffect(controller, token) {
        onDispose { controller.unregister(token) }
    }
}

@Composable
fun UnsavedChangesDialog(controller: UnsavedChangesController) {
    val s = pharmStrings
    PharmModal(
        open = controller.dialogOpen,
        onDismiss = controller::keepEditing,
        title = s.commonUnsavedChangesTitle,
        subtitle = s.commonUnsavedChangesMessage,
        size = PharmModalSize.Sm,
        footer = {
            PharmButton(
                label = s.commonKeepEditing,
                onClick = controller::keepEditing,
                variant = PharmButtonVariant.Outline,
            )
            PharmButton(
                label = s.commonDiscardChanges,
                onClick = controller::discardChanges,
                variant = PharmButtonVariant.Danger,
            )
        },
    ) {}
}
