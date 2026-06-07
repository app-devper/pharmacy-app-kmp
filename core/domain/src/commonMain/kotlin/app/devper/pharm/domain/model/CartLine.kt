package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money
import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.domain.extension.resolvePrice

data class CartLine(
    val drug: Drug,
    val qty: Int,
    val tier: String = Tier.Retail,
    val discount: Money = Money.Zero,
    val selectedUnit: AltUnit? = null,
) {

    val key: CartLineKey get() = CartLineKey(drug.id, selectedUnit?.name)

    val displayUnit: String get() = selectedUnit?.name ?: drug.unit ?: "หน่วย"

    val displayQty: Int get() = qty / factor

    val factor: Int get() = selectedUnit?.factor?.coerceAtLeast(1) ?: 1

    val basePrice: Money
        get() = if (selectedUnit != null) {
            resolvePrice(selectedUnit.sellPrice, selectedUnit.prices, tier) / factor
        } else {
            resolvePrice(drug.sellPrice, drug.prices, tier)
        }

    val unitPrice: Money get() = basePrice * factor

    val effectiveUnitPrice: Money
        get() = (unitPrice - discount * factor).coerceAtLeast(Money.Zero)

    val lineTotal: Money get() = (basePrice - discount).coerceAtLeast(Money.Zero) * qty
}

data class CartLineKey(
    val drugId: String,
    val altUnitName: String?,
)
