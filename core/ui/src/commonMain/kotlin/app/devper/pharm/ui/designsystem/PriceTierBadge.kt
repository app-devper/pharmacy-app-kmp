package app.devper.pharm.ui.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.ui.i18n.PharmStrings
import app.devper.pharm.ui.i18n.pharmStrings

internal fun showsPriceTierBadge(priceTier: String): Boolean =
    priceTier.isNotBlank() && priceTier != Tier.Retail

internal fun priceTierLabel(priceTier: String, s: PharmStrings): String = when (priceTier) {
    Tier.Wholesale -> s.sellTierWholesaleLabel
    Tier.Regular -> s.sellTierRegularLabel
    else -> priceTier
}

@Composable
fun PriceTierBadge(
    priceTier: String,
    modifier: Modifier = Modifier,
    size: PharmBadgeSize = PharmBadgeSize.Md,
) {
    if (!showsPriceTierBadge(priceTier)) return
    PharmBadge(
        text = priceTierLabel(priceTier, pharmStrings),
        tone = PharmBadgeTone.Purple,
        size = size,
        modifier = modifier,
    )
}
