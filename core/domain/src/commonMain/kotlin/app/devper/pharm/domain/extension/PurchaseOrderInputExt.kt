package app.devper.pharm.domain.extension

import app.devper.pharm.domain.param.PurchaseOrderItemInput
import app.devper.pharm.domain.validation.Check
import app.devper.pharm.domain.validation.Field

fun buildPurchaseOrderItemInput(
    drugId: String,
    drugName: String,
    lotNumber: String,
    expiryDate: String,
    qty: String,
    costPrice: String,
    sellPrice: String,
): Result<PurchaseOrderItemInput> = runCatching {
    val parsedDrugId = Field.notBlank(drugId, "ยา")
    val parsedLotNumber = Field.notBlank(lotNumber, "lot number")
    val parsedExpiry = Field.localDate(expiryDate, label = "วันหมดอายุ")
    val parsedQty = Field.positiveInt(qty)
    PurchaseOrderItemInput(
        drugId = parsedDrugId,
        drugName = drugName.trim(),
        lotNumber = parsedLotNumber,
        expiryDate = parsedExpiry,
        qty = parsedQty,
        costPrice = costPrice.toDoubleOrNull() ?: 0.0,
        sellPrice = sellPrice.toDoubleOrNull(),
    )
}

fun isPurchaseOrderLineValid(
    drugId: String,
    lotNumber: String,
    expiryDate: String,
    qty: String,
): Boolean =
    Check.notBlank(drugId) &&
        Check.notBlank(lotNumber) &&
        Check.localDate(expiryDate) &&
        Check.positiveInt(qty)
