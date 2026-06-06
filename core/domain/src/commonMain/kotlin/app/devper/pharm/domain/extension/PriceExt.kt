package app.devper.pharm.domain.extension

object Tier {
    const val Retail    = "retail"
    const val Regular   = "regular"
    const val Wholesale = "wholesale"
}

fun resolvePrice(base: Double, prices: Map<String, Double>?, tier: String): Double {
    if (prices.isNullOrEmpty()) return base
    prices[tier]?.let { return it }
    prices[Tier.Retail]?.let { return it }
    return base
}

fun tierLabel(tier: String): String = when (tier) {
    Tier.Wholesale -> "ขายส่ง"
    Tier.Regular   -> "ขายปลีก"
    Tier.Retail, "" -> "ราคาปลีก"
    else -> tier
}
