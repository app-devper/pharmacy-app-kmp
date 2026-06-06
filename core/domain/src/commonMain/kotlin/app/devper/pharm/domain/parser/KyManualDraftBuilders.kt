package app.devper.pharm.domain.parser

import app.devper.pharm.domain.model.KyForm

object Ky10DraftBuilder {

    fun build(
        date: String,
        drugName: String,
        regNo: String,
        qty: String,
        unit: String,
        buyerName: String,
        buyerAddress: String,
        rxNo: String,
        doctor: String,
        balance: String,
    ): Result<KyForm.Ky10> = runCatching {
        validateCommon(date = date, drugName = drugName, unit = unit, qty = qty)
        val parsedQty = qty.toInt()
        val parsedBalance = balance.toIntOrNull() ?: 0
        require(parsedBalance >= 0) { "ยอดคงเหลือต้องไม่ติดลบ" }
        KyForm.Ky10(
            saleId = "",
            date = date.trim(),
            drugName = drugName.trim(),
            regNo = regNo.trim(),
            qty = parsedQty,
            unit = unit.trim(),
            buyerName = buyerName.trim(),
            buyerAddress = buyerAddress.trim(),
            rxNo = rxNo.trim(),
            doctor = doctor.trim(),
            balance = parsedBalance,
        )
    }

    fun isDraftValid(date: String, drugName: String, unit: String, qty: String): Boolean =
        commonValid(date, drugName, unit, qty)
}

object Ky11DraftBuilder {

    fun build(
        date: String,
        drugName: String,
        regNo: String,
        qty: String,
        unit: String,
        buyerName: String,
        purpose: String,
        pharmacist: String,
    ): Result<KyForm.Ky11> = runCatching {
        validateCommon(date = date, drugName = drugName, unit = unit, qty = qty)
        KyForm.Ky11(
            saleId = "",
            date = date.trim(),
            drugName = drugName.trim(),
            regNo = regNo.trim(),
            qty = qty.toInt(),
            unit = unit.trim(),
            buyerName = buyerName.trim(),
            purpose = purpose.trim(),
            pharmacist = pharmacist.trim(),
        )
    }

    fun isDraftValid(date: String, drugName: String, unit: String, qty: String): Boolean =
        commonValid(date, drugName, unit, qty)
}

object Ky12DraftBuilder {

    fun build(
        date: String,
        drugName: String,
        regNo: String,
        qty: String,
        unit: String,
        rxNo: String,
        patientName: String,
        doctor: String,
        hospital: String,
        totalValue: String,
        status: String,
    ): Result<KyForm.Ky12> = runCatching {
        validateCommon(date = date, drugName = drugName, unit = unit, qty = qty)
        val parsedValue = totalValue.toDoubleOrNull() ?: 0.0
        require(parsedValue >= 0.0) { "มูลค่ารวมต้องไม่ติดลบ" }
        KyForm.Ky12(
            saleId = "",
            date = date.trim(),
            drugName = drugName.trim(),
            regNo = regNo.trim(),
            qty = qty.toInt(),
            unit = unit.trim(),
            rxNo = rxNo.trim(),
            patientName = patientName.trim(),
            doctor = doctor.trim(),
            hospital = hospital.trim(),
            totalValue = parsedValue,
            status = status.trim().ifBlank { "จ่ายแล้ว" },
        )
    }

    fun isDraftValid(date: String, drugName: String, unit: String, qty: String): Boolean =
        commonValid(date, drugName, unit, qty)
}

private fun validateCommon(date: String, drugName: String, unit: String, qty: String) {
    require(date.isNotBlank()) { "ต้องระบุวันที่" }
    require(drugName.isNotBlank()) { "ต้องระบุชื่อยา" }
    require(unit.isNotBlank()) { "ต้องระบุหน่วย" }
    val parsedQty = qty.toIntOrNull() ?: error("จำนวนต้องเป็นตัวเลข")
    require(parsedQty > 0) { "จำนวนต้องมากกว่า 0" }
}

private fun commonValid(date: String, drugName: String, unit: String, qty: String): Boolean =
    date.isNotBlank() &&
        drugName.isNotBlank() &&
        unit.isNotBlank() &&
        (qty.toIntOrNull() ?: 0) > 0
