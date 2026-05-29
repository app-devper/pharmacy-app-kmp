package app.devper.pharm.ui.print

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.model.LabelSize
import app.devper.pharm.ui.format.formatBahtCurrency
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.TabularNumbers
import app.devper.pharm.ui.theme.pharmTokens

@Immutable
data class PharmLabelStyle(
    val mmToDpScale: Float = 3f,
    val padding: Dp = 4.dp,
    val barcodeBarHeight: Dp = 8.dp,
    val paperBg: Color? = null,
    val inkColor: Color? = null,
    val mutedInkColor: Color? = null,
    val accentColor: Color? = null,
    val borderColor: Color? = null,
    val fontFamily: FontFamily? = null,
)

@Composable
fun PharmLabelCard(
    line: LabelLine,
    size: LabelSize,
    style: PharmLabelStyle = PharmLabelStyle(),
    modifier: Modifier = Modifier,
) {
    val t = pharmTokens
    val paper = style.paperBg ?: t.colors.surface
    val ink = style.inkColor ?: t.colors.fg1
    val muted = style.mutedInkColor ?: t.colors.fg3
    val accent = style.accentColor ?: t.colors.accent
    val borderColor = style.borderColor ?: t.colors.border
    val fontFamily = style.fontFamily

    Column(
        modifier = modifier
            .width((size.widthMm * style.mmToDpScale).dp)
            .height((size.heightMm * style.mmToDpScale).dp)
            .background(paper, t.shapes.sm)
            .border(1.dp, borderColor, t.shapes.sm)
            .padding(style.padding),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = line.drugName,
            style = PharmText.micro.copy(
                color = ink,
                fontWeight = FontWeight.SemiBold,
                fontFamily = fontFamily,
            ),
            maxLines = 2,
        )
        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
            if (line.includePrice) {
                Text(
                    text = formatBahtCurrency(line.price),
                    style = PharmText.body.copy(
                        color = accent,
                        fontWeight = FontWeight.Bold,
                        fontFamily = fontFamily,
                        fontFeatureSettings = TabularNumbers,
                    ),
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(style.barcodeBarHeight)
                    .background(ink),
            )
            Text(
                text = line.barcode,
                style = PharmText.micro.copy(
                    color = muted,
                    fontFamily = fontFamily,
                    fontFeatureSettings = TabularNumbers,
                ),
                maxLines = 1,
            )
        }
    }
}
