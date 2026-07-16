package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PharmBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    dismissEnabled: Boolean = true,
    maxWidth: Dp = 640.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    val currentDismissEnabled by rememberUpdatedState(dismissEnabled)
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { target ->
            canChangeBottomSheetValue(
                dismissEnabled = currentDismissEnabled,
                isHiddenTarget = target == SheetValue.Hidden,
            )
        },
    )
    val t = pharmTokens

    ModalBottomSheet(
        onDismissRequest = { if (dismissEnabled) onDismissRequest() },
        modifier = modifier,
        sheetState = sheetState,
        sheetMaxWidth = maxWidth,
        containerColor = t.colors.surface,
        scrimColor = t.colors.scrim,
        contentWindowInsets = {
            WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom)
        },
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().imePadding(),
            content = content,
        )
    }
}

internal fun canChangeBottomSheetValue(
    dismissEnabled: Boolean,
    isHiddenTarget: Boolean,
): Boolean = dismissEnabled || !isHiddenTarget
