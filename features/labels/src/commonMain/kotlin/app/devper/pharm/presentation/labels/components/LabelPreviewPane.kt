package app.devper.pharm.presentation.labels.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.pharmTokens

@Composable
internal fun LabelPreviewPane(size: LabelSize, line: LabelLine?, modifier: Modifier = Modifier) {
    val t = pharmTokens
    if (line == null) return
    Column(
        modifier = modifier
            .clip(t.shapes.lg)
            .background(t.colors.surface)
            .border(1.dp, t.colors.borderSubtle, t.shapes.lg)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Text(
            text = "ตัวอย่าง (${size.label})",
            style = PharmText.micro.copy(color = t.colors.fg2, fontWeight = FontWeight.SemiBold),
        )
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            repeat(6) { LabelPreviewCard(size = size, line = line) }
        }
    }
}

@Composable
private fun LabelPreviewCard(size: LabelSize, line: LabelLine) {
    val t = pharmTokens
    val cardW = (size.widthMm * 3).dp
    val cardH = (size.heightMm * 3).dp
    Column(
        modifier = Modifier
            .width(cardW)
            .height(cardH)
            .border(1.dp, t.colors.border, t.shapes.sm)
            .padding(4.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = line.drugName,
            style = PharmText.micro.copy(color = t.colors.fg1, fontWeight = FontWeight.SemiBold),
            maxLines = 2,
        )
        Column {
            if (line.includePrice) {
                Text(
                    text = "฿${formatMoney(line.price)}",
                    style = PharmText.body.copy(color = t.colors.accent, fontWeight = FontWeight.Bold),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(t.colors.fg1),
            )
            Text(
                text = line.barcode,
                style = PharmText.micro.copy(color = t.colors.fg3),
                maxLines = 1,
            )
        }
    }
}

private fun formatMoney(v: Double): String {
    val cents = (v * 100.0 + if (v >= 0) 0.5 else -0.5).toLong()
    val whole = cents / 100
    val frac = (cents % 100).let { if (it < 0) -it else it }.toString().padStart(2, '0')
    return "$whole.$frac"
}
