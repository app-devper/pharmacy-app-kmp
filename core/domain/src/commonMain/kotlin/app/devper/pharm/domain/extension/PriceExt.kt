package app.devper.pharm.domain.extension

import app.devper.pharm.common.value.Money

object Tier {
    const val Retail    = "retail"
    const val Regular   = "regular"
    const val Wholesale = "wholesale"
}

fun resolvePrice(base: Money, prices: Map<String, Money>?, tier: String): Money {
    if (prices.isNullOrEmpty()) return base
    prices[tier]?.let { return it }
    prices[Tier.Retail]?.let { return it }
    return base
}

