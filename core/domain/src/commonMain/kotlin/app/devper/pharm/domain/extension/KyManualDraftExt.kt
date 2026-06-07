package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.KyForm
import kotlinx.datetime.LocalDate

fun buildKy10Draft(
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
    val parsedDate = validateKyManualCommon(date = date, drugName = drugName, unit = unit, qty = qty)
    val parsedQty = qty.toInt()
    val parsedBalance = balance.toIntOrNull() ?: 0
    require(parsedBalance >= 0) { "ยอดคงเหลือต้องไม่ติดลบ" }
    KyForm.Ky10(
        saleId = "",
        date = parsedDate,
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

fun isKy10DraftValid(date: String, drugName: String, unit: String, qty: String): Boolean =
    isKyManualCommonValid(date, drugName, unit, qty)

fun buildKy11Draft(
    date: String,
    drugName: String,
    regNo: String,
    qty: String,
    unit: String,
    buyerName: String,
    purpose: String,
    pharmacist: String,
): Result<KyForm.Ky11> = runCatching {
    val parsedDate = validateKyManualCommon(date = date, drugName = drugName, unit = unit, qty = qty)
    KyForm.Ky11(
        saleId = "",
        date = parsedDate,
        drugName = drugName.trim(),
        regNo = regNo.trim(),
        qty = qty.toInt(),
        unit = unit.trim(),
        buyerName = buyerName.trim(),
        purpose = purpose.trim(),
        pharmacist = pharmacist.trim(),
    )
}

fun isKy11DraftValid(date: String, drugName: String, unit: String, qty: String): Boolean =
    isKyManualCommonValid(date, drugName, unit, qty)

fun buildKy12Draft(
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
    val parsedDate = validateKyManualCommon(date = date, drugName = drugName, unit = unit, qty = qty)
    val parsedValue = totalValue.toDoubleOrNull() ?: 0.0
    require(parsedValue >= 0.0) { "มูลค่ารวมต้องไม่ติดลบ" }
    KyForm.Ky12(
        saleId = "",
        date = parsedDate,
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

fun isKy12DraftValid(date: String, drugName: String, unit: String, qty: String): Boolean =
    isKyManualCommonValid(date, drugName, unit, qty)

private fun validateKyManualCommon(date: String, drugName: String, unit: String, qty: String): LocalDate {
    require(date.isNotBlank()) { "ต้องระบุวันที่" }
    require(drugName.isNotBlank()) { "ต้องระบุชื่อยา" }
    require(unit.isNotBlank()) { "ต้องระบุหน่วย" }
    val parsedDate = runCatching { LocalDate.parse(date.trim()) }.getOrNull()
        ?: error("วันที่ไม่ถูกต้อง (รูปแบบ YYYY-MM-DD)")
    val parsedQty = qty.toIntOrNull() ?: error("จำนวนต้องเป็นตัวเลข")
    require(parsedQty > 0) { "จำนวนต้องมากกว่า 0" }
    return parsedDate
}

private fun isKyManualCommonValid(date: String, drugName: String, unit: String, qty: String): Boolean =
    date.isNotBlank() &&
        drugName.isNotBlank() &&
        unit.isNotBlank() &&
        (qty.toIntOrNull() ?: 0) > 0
