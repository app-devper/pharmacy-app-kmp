package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.pharmTokens

@Composable
fun PharmToggleSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val t = pharmTokens
    val trackBg = if (checked) t.colors.accent else t.colors.border
    val knobAlign = if (checked) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        modifier = modifier
            .width(36.dp)
            .height(20.dp)
            .clip(t.shapes.pill)
            .background(trackBg)
            .clickable(enabled = enabled, onClick = { onCheckedChange(!checked) })
            .padding(2.dp),
        contentAlignment = knobAlign,
    ) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(t.shapes.pill)
                .background(t.colors.surface),
        )
    }
}
