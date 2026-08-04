package app.devper.pharm.domain.validation

import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.validation.Check
import app.devper.pharm.domain.validation.Field
import app.devper.pharm.domain.validation.FieldLabel
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
    val parsedBalance = Field.nonNegativeIntOrDefault(balance, default = 0, label = FieldLabel.Balance)
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
    val parsedValue = Field.nonNegativeDoubleOrDefault(totalValue, default = 0.0, label = FieldLabel.TotalValue)
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
    val parsedDate = Field.localDate(date)
    Field.notBlank(drugName, FieldLabel.DrugName)
    Field.notBlank(unit, FieldLabel.Unit)
    Field.positiveInt(qty)
    return parsedDate
}

private fun isKyManualCommonValid(date: String, drugName: String, unit: String, qty: String): Boolean =
    Check.localDate(date) &&
        Check.notBlank(drugName) &&
        Check.notBlank(unit) &&
        Check.positiveInt(qty)
