@file:OptIn(androidx.compose.foundation.layout.ExperimentalLayoutApi::class)

package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.i18n.pharmStrings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.theme.PharmText
import app.devper.pharm.ui.theme.fmtBaht
import app.devper.pharm.domain.extension.EXPIRY_WARNING_DAYS
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
    expiryDaysLeft: Int? = null,
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

    val s = pharmStrings
    val (typeTone, typeLabel) = when (type) {
        DrugCardType.Herb       -> PharmBadgeTone.Emerald to s.commonDrugTypeHerb
        DrugCardType.Supplement -> PharmBadgeTone.Orange  to s.commonDrugTypeSupplement
        DrugCardType.Rx         -> PharmBadgeTone.Purple  to s.commonDrugTypeRx
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
            .clickable(role = Role.Button, onClick = onClick)
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
                oversold -> PharmBadge(text = s.commonOversoldBadge(-stock), tone = PharmBadgeTone.Red, size = PharmBadgeSize.Sm)
                empty    -> PharmBadge(text = s.commonPresellBadge, tone = PharmBadgeTone.Amber, size = PharmBadgeSize.Sm)
            }
            when {
                expiryDaysLeft == null -> Unit
                expiryDaysLeft < 0 -> PharmBadge(text = s.commonExpiredBadge, tone = PharmBadgeTone.Red, size = PharmBadgeSize.Sm)
                expiryDaysLeft <= EXPIRY_WARNING_DAYS -> PharmBadge(text = s.commonExpiresInBadge(expiryDaysLeft), tone = PharmBadgeTone.Amber, size = PharmBadgeSize.Sm)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

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
                text = s.commonStockRemaining(stock, unit),
                style = PharmText.micro.copy(color = stockColor),
            )
        }
    }
}
