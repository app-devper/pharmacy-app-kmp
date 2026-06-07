package app.devper.pharm.domain.extension

import app.devper.pharm.domain.param.AddKy9Param
import app.devper.pharm.domain.validation.Check
import app.devper.pharm.domain.validation.Field

fun buildKy9Draft(
    date: String,
    drugName: String,
    regNo: String,
    unit: String,
    qty: String,
    pricePerUnit: String,
    seller: String,
    invoiceNo: String,
): Result<AddKy9Param> = runCatching {
    val parsedDate = Field.localDate(date)
    val parsedDrugName = Field.notBlank(drugName, "ชื่อยา")
    val parsedUnit = Field.notBlank(unit, "หน่วย")
    val parsedQty = Field.positiveInt(qty)
    val parsedPrice = Field.nonNegativeDouble(pricePerUnit, label = "ราคาต่อหน่วย")
    AddKy9Param(
        date = parsedDate,
        drugName = parsedDrugName,
        regNo = regNo.trim(),
        unit = parsedUnit,
        qty = parsedQty,
        pricePerUnit = parsedPrice,
        seller = seller.trim(),
        invoiceNo = invoiceNo.trim(),
    )
}

fun isKy9DraftValid(
    date: String,
    drugName: String,
    unit: String,
    qty: String,
    pricePerUnit: String,
): Boolean =
    Check.localDate(date) &&
        Check.notBlank(drugName) &&
        Check.notBlank(unit) &&
        Check.positiveInt(qty) &&
        Check.nonNegativeDouble(pricePerUnit)
