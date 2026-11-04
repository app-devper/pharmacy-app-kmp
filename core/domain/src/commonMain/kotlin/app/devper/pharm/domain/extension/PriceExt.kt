package app.devper.pharm.domain.extension

import app.devper.pharm.common.value.Money

object Tier {
    const val Retail    = "retail"
    const val Regular   = "regular"
    const val Wholesale = "wholesale"
}

/**
 * The price of [tier], else the retail price, else [base]. A tier price that is
 * not positive is unset, as pharmacy-api's ResolveTierPrice treats it.
 */
fun resolvePrice(base: Money, prices: Map<String, Money>?, tier: String): Money {
    if (prices.isNullOrEmpty()) return base
    prices[tier]?.takeIf { it.isPositive }?.let { return it }
    prices[Tier.Retail]?.takeIf { it.isPositive }?.let { return it }
    return base
}

