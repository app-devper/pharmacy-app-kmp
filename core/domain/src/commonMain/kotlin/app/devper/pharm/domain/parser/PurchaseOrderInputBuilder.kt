package app.devper.pharm.domain.parser

import app.devper.pharm.domain.param.PurchaseOrderItemInput

object PurchaseOrderInputBuilder {

    fun build(
        drugId: String,
        drugName: String,
        lotNumber: String,
        expiryDate: String,
        qty: String,
        costPrice: String,
        sellPrice: String,
    ): Result<PurchaseOrderItemInput> = runCatching {
        require(drugId.isNotBlank()) { "ต้องเลือกยา" }
        require(lotNumber.isNotBlank()) { "ต้องระบุ lot number" }
        require(expiryDate.isNotBlank()) { "ต้องระบุวันหมดอายุ" }
        val parsedQty = qty.toIntOrNull() ?: error("จำนวนต้องเป็นตัวเลข")
        require(parsedQty > 0) { "จำนวนต้องมากกว่า 0" }
        PurchaseOrderItemInput(
            drugId = drugId,
            drugName = drugName.trim(),
            lotNumber = lotNumber.trim(),
            expiryDate = expiryDate.trim(),
            qty = parsedQty,
            costPrice = costPrice.toDoubleOrNull() ?: 0.0,
            sellPrice = sellPrice.toDoubleOrNull(),
        )
    }

    fun isLineValid(
        drugId: String,
        lotNumber: String,
        expiryDate: String,
        qty: String,
    ): Boolean =
        drugId.isNotBlank() &&
            lotNumber.isNotBlank() &&
            expiryDate.isNotBlank() &&
            (qty.toIntOrNull() ?: 0) > 0
}
