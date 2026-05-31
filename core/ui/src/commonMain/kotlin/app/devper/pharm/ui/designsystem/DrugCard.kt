@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.devper.pharm.ui.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.ui.theme.pharmTokens

enum class DrugCardType { Rx, Herb, Supplement }

@Composable
fun DrugCard(
    name: String,
    generic: String?,
    price: Double,
    stock: Int,
    unit: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    type: DrugCardType = DrugCardType.Rx,
    altUnitCount: Int = 0,
    kyForm: Int? = null,
    highlighted: Boolean = false,
    lowStockThreshold: Int = 20,
) {
    val t = pharmTokens
    val oversold = stock < 0
    val empty = stock == 0
    val low = stock > 0 && stock <= lowStockThreshold

    val borderColor: Color = when {
        highlighted -> t.colors.successFg
        oversold    -> t.colors.dangerFg
        empty       -> t.colors.warningBg
        else        -> t.colors.border
    }
    val ringColor: Color? = if (highlighted) t.colors.successFg.copy(alpha = 0.4f) else null

    val (typeTone, typeLabel) = when (type) {
        DrugCardType.Herb       -> PharmBadgeTone.Emerald to "ยาสมุนไพร"
        DrugCardType.Supplement -> PharmBadgeTone.Orange  to "อาหารเสริม"
        DrugCardType.Rx         -> PharmBadgeTone.Purple  to "ยาแผนปัจจุบัน"
    }

    val shape = t.shapes.lg

    val baseMod = modifier
        .fillMaxWidth()
        .scale(if (highlighted) 1.02f else 1f)
        .clip(shape)
        .background(t.colors.surface, shape)
        .border(1.dp, borderColor, shape)
    val withRing = if (ringColor != null) baseMod.border(2.dp, ringColor, shape) else baseMod

    Column(
        modifier = withRing
            .clickable(onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = name,
                    style = PharmText.body.copy(
                        color = t.colors.fg1,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (!generic.isNullOrBlank()) {
                    Text(
                        text = generic,
                        style = PharmText.micro.copy(color = t.colors.fgMuted),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (altUnitCount > 0) {
                PharmBadge(
                    text = "+$altUnitCount",
                    tone = PharmBadgeTone.Indigo,
                    size = PharmBadgeSize.Sm,
                )
            }
        }

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            PharmBadge(text = typeLabel, tone = typeTone, size = PharmBadgeSize.Sm)
            if (kyForm != null) KyBadge(form = kyForm)
            when {
                oversold -> PharmBadge(text = "ขายเกิน ${-stock}", tone = PharmBadgeTone.Red, size = PharmBadgeSize.Sm)
                empty    -> PharmBadge(text = "ขายล่วงหน้า", tone = PharmBadgeTone.Amber, size = PharmBadgeSize.Sm)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom,
        ) {
            Text(text = fmtBaht(price), style = PharmText.price)
            val stockColor = when {
                oversold -> t.colors.dangerFg
                empty -> t.colors.warningFg
                low -> t.colors.warningFg
                else -> t.colors.fgMuted
            }
            Text(
                text = "คงเหลือ $stock $unit",
                style = PharmText.micro.copy(color = stockColor),
            )
        }
    }
}
