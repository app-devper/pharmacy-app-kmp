package app.devper.pharm.domain.parser

import app.devper.pharm.domain.param.AddKy9Param

object Ky9DraftBuilder {

    fun build(
        date: String,
        drugName: String,
        regNo: String,
        unit: String,
        qty: String,
        pricePerUnit: String,
        seller: String,
        invoiceNo: String,
    ): Result<AddKy9Param> = runCatching {
        require(date.isNotBlank()) { "ต้องระบุวันที่" }
        require(drugName.isNotBlank()) { "ต้องระบุชื่อยา" }
        require(unit.isNotBlank()) { "ต้องระบุหน่วย" }
        val parsedQty = qty.toIntOrNull() ?: error("จำนวนต้องเป็นตัวเลข")
        require(parsedQty > 0) { "จำนวนต้องมากกว่า 0" }
        val parsedPrice = pricePerUnit.toDoubleOrNull() ?: error("ราคาต่อหน่วยต้องเป็นตัวเลข")
        require(parsedPrice >= 0.0) { "ราคาต่อหน่วยต้องไม่ติดลบ" }
        AddKy9Param(
            date = date.trim(),
            drugName = drugName.trim(),
            regNo = regNo.trim(),
            unit = unit.trim(),
            qty = parsedQty,
            pricePerUnit = parsedPrice,
            seller = seller.trim(),
            invoiceNo = invoiceNo.trim(),
        )
    }

    fun isDraftValid(
        date: String,
        drugName: String,
        unit: String,
        qty: String,
        pricePerUnit: String,
    ): Boolean =
        date.isNotBlank() &&
            drugName.isNotBlank() &&
            unit.isNotBlank() &&
            (qty.toIntOrNull() ?: 0) > 0 &&
            (pricePerUnit.toDoubleOrNull() ?: -1.0) >= 0.0
}
