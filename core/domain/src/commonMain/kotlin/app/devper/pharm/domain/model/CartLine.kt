package app.devper.pharm.domain.model

import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.domain.extension.resolvePrice

data class CartLine(
    val drug: Drug,
    val qty: Int,
    val tier: String = Tier.Retail,
    val discount: Double = 0.0,
    val selectedUnit: AltUnit? = null,
) {

    val key: CartLineKey get() = CartLineKey(drug.id, selectedUnit?.name)

    val displayUnit: String get() = selectedUnit?.name ?: drug.unit ?: "หน่วย"

    val displayQty: Int get() = qty / factor

    val factor: Int get() = selectedUnit?.factor?.coerceAtLeast(1) ?: 1

    val basePrice: Double
        get() = if (selectedUnit != null) {
            resolvePrice(selectedUnit.sellPrice, selectedUnit.prices, tier) / factor
        } else {
            resolvePrice(drug.sellPrice, drug.prices, tier)
        }

    val unitPrice: Double get() = basePrice * factor

    val effectiveUnitPrice: Double
        get() = (unitPrice - discount * factor).coerceAtLeast(0.0)

    val lineTotal: Double get() = (basePrice - discount).coerceAtLeast(0.0) * qty
}

data class CartLineKey(
    val drugId: String,
    val altUnitName: String?,
)
