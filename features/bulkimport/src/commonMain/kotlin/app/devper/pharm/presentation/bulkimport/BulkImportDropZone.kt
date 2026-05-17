package app.devper.pharm.presentation.bulkimport

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.designsystem.PharmButton
import app.devper.pharm.ui.designsystem.PharmButtonSize
import app.devper.pharm.ui.designsystem.PharmButtonVariant
import app.devper.pharm.ui.designsystem.PharmIcons
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun BulkImportDropZone(
    onPickFile: () -> Unit,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 168.dp,
) {
    val t = pharmTokens
    val shape = t.shapes.lg
    val border = t.colors.border

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(shape)
            .background(t.colors.accentBgSoft.copy(alpha = 0.35f), shape)
            .dashedBorder(border, shape = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(PaddingValues(24.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Icon(
                imageVector = PharmIcons.Imports,
                contentDescription = null,
                tint = t.colors.accent,
                modifier = Modifier.size(32.dp),
            )
            Text(
                text = "ลากไฟล์ JSON มาวางที่นี่ หรือกดเลือกไฟล์",
                style = PharmText.body.copy(color = t.colors.fg1),
                textAlign = TextAlign.Center,
            )
            Text(
                text = "รองรับ array หรือ {\"drugs\": [...]} สูงสุด 1,000 รายการ",
                style = PharmText.micro.copy(color = t.colors.fg3),
                textAlign = TextAlign.Center,
            )
            Spacer(Modifier.height(4.dp))
            PharmButton(
                label = "เลือกไฟล์",
                onClick = onPickFile,
                variant = PharmButtonVariant.Outline,
                size = PharmButtonSize.Sm,
                leadingIcon = { Icon(PharmIcons.Imports, contentDescription = null) },
            )
        }
    }
}

private fun Modifier.dashedBorder(
    color: Color,
    shape: androidx.compose.ui.unit.Dp,
    strokeWidth: androidx.compose.ui.unit.Dp = 1.5.dp,
    dashOn: androidx.compose.ui.unit.Dp = 6.dp,
    dashOff: androidx.compose.ui.unit.Dp = 4.dp,
): Modifier = this.drawBehind {
    val stroke = strokeWidth.toPx()
    val on = dashOn.toPx()
    val off = dashOff.toPx()
    val r = CornerRadius(shape.toPx(), shape.toPx())
    drawRoundRect(
        color = color,
        topLeft = androidx.compose.ui.geometry.Offset(stroke / 2f, stroke / 2f),
        size = Size(size.width - stroke, size.height - stroke),
        cornerRadius = r,
        style = Stroke(width = stroke, pathEffect = PathEffect.dashPathEffect(floatArrayOf(on, off), 0f)),
    )
}
