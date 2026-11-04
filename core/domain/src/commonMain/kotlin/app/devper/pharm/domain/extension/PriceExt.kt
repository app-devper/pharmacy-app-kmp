package app.devper.pharm.domain.extension

import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.model.AltUnit
import app.devper.pharm.domain.model.Drug

object Tier {
    const val Retail    = "retail"
    const val Regular   = "regular"
    const val Wholesale = "wholesale"
}

fun resolvePrice(base: Money, prices: Map<String, Money>?, tier: String): Money {
    if (prices.isNullOrEmpty()) return base
    prices[tier]?.takeIf { it.isPositive }?.let { return it }
    prices[Tier.Retail]?.takeIf { it.isPositive }?.let { return it }
    return base
}

fun basePriceOf(drug: Drug, unit: AltUnit?, tier: String): Money {
    if (unit == null) return resolvePrice(drug.sellPrice, drug.prices, tier)
    return (resolvePrice(unit.sellPrice, unit.prices, tier) / unit.factor.coerceAtLeast(1)).roundToSatang()
}

fun linePrice(drug: Drug, unit: AltUnit?, tier: String): Money =
    basePriceOf(drug, unit, tier) * (unit?.factor?.coerceAtLeast(1) ?: 1)
