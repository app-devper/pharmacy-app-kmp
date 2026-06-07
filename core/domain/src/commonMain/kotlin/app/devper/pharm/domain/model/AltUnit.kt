package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Money

data class AltUnit(
    val name: String,
    val factor: Int,
    val sellPrice: Money,
    val prices: Map<String, Money> = emptyMap(),
    val barcode: String? = null,
    val hidden: Boolean = false,
)
