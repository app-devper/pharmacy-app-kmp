package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmSaveAction(
    saving: Boolean,
    canSubmit: Boolean,
    onSubmit: () -> Unit,
    label: String = pharmStrings.commonSave,
) {
    val focusManager = LocalFocusManager.current
    if (saving) {
        PharmCircularProgress(
            color = pharmTokens.colors.accent,
            strokeWidth = 2.dp,
            modifier = Modifier.size(20.dp),
        )
    } else {
        PharmButton(
            label = label,
            onClick = {
                focusManager.clearFocus()
                onSubmit()
            },
            enabled = canSubmit,
            size = PharmButtonSize.Sm,
        )
    }
}
