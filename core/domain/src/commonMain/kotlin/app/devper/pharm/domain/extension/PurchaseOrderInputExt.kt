package app.devper.pharm.domain.extension

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.param.PurchaseOrderItemInput
import app.devper.pharm.domain.validation.Check
import app.devper.pharm.domain.validation.Field
import app.devper.pharm.domain.validation.FieldLabel

fun buildPurchaseOrderItemInput(
    drugId: String,
    drugName: String,
    lotNumber: String,
    expiryDate: String,
    qty: String,
    costPrice: String,
    sellPrice: String,
): Result<PurchaseOrderItemInput> = runCatching {
    val parsedDrugId = Field.notBlank(drugId, FieldLabel.Drug)
    val parsedLotNumber = Field.notBlank(lotNumber, FieldLabel.LotNumber)
    val parsedExpiry = Field.localDate(expiryDate, label = FieldLabel.ExpiryDate)
    val parsedQty = Field.positiveInt(qty)
    PurchaseOrderItemInput(
        drugId = parsedDrugId,
        drugName = drugName.trim(),
        lotNumber = parsedLotNumber,
        expiryDate = parsedExpiry,
        qty = Quantity(parsedQty),
        costPrice = Money(costPrice.toDoubleOrNull() ?: 0.0),
        sellPrice = sellPrice.toDoubleOrNull()?.let(::Money),
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
