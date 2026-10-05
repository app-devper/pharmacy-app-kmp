package app.devper.pharm.data.storage

import app.devper.pharm.domain.model.KyForm
import kotlinx.datetime.LocalDate

internal fun KyForm.toPendingDto(): PendingKyFormDto = when (this) {
    is KyForm.Ky10 -> PendingKyFormDto(
        form = "ky10", saleId = saleId, date = date.toString(), drugName = drugName, regNo = regNo,
        qty = qty, unit = unit, buyerName = buyerName, buyerAddress = buyerAddress, rxNo = rxNo,
        doctor = doctor, balance = balance,
    )
    is KyForm.Ky11 -> PendingKyFormDto(
        form = "ky11", saleId = saleId, date = date.toString(), drugName = drugName, regNo = regNo,
        qty = qty, unit = unit, buyerName = buyerName, purpose = purpose, pharmacist = pharmacist,
    )
    is KyForm.Ky12 -> PendingKyFormDto(
        form = "ky12", saleId = saleId, date = date.toString(), drugName = drugName, regNo = regNo,
        qty = qty, unit = unit, rxNo = rxNo, patientName = patientName, doctor = doctor,
        hospital = hospital, totalValue = totalValue, status = status,
    )
}

/** Null for a form this version does not know; it is dropped rather than failing the queue. */
internal fun PendingKyFormDto.toDomainOrNull(): KyForm? {
    val day = runCatching { LocalDate.parse(date) }.getOrNull() ?: return null
    return when (form) {
        "ky10" -> KyForm.Ky10(
            saleId = saleId, date = day, drugName = drugName, regNo = regNo, qty = qty, unit = unit,
            buyerName = buyerName, buyerAddress = buyerAddress, rxNo = rxNo, doctor = doctor, balance = balance,
        )
        "ky11" -> KyForm.Ky11(
            saleId = saleId, date = day, drugName = drugName, regNo = regNo, qty = qty, unit = unit,
            buyerName = buyerName, purpose = purpose, pharmacist = pharmacist,
        )
        "ky12" -> KyForm.Ky12(
            saleId = saleId, date = day, drugName = drugName, regNo = regNo, qty = qty, unit = unit,
            rxNo = rxNo, patientName = patientName, doctor = doctor, hospital = hospital,
            totalValue = totalValue, status = status,
        )
        else -> null
    }
}
