package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.internal.parseLocalDateOrNull
import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.dto.Ky10Dto
import app.devper.pharm.data.remote.dto.Ky11Dto
import app.devper.pharm.data.remote.dto.Ky12Dto
import app.devper.pharm.data.remote.dto.Ky9Dto
import app.devper.pharm.domain.model.Ky10Entry
import app.devper.pharm.domain.model.Ky11Entry
import app.devper.pharm.domain.model.Ky12Entry
import app.devper.pharm.domain.model.Ky9Entry

internal fun Ky9Dto.toDomain(): Ky9Entry = Ky9Entry(
    id = id,
    saleId = saleId,
    date = date.parseLocalDateOrNull(),
    drugName = drugName,
    regNo = regNo,
    unit = unit,
    qty = qty,
    pricePerUnit = pricePerUnit,
    totalValue = totalValue,
    seller = seller,
    invoiceNo = invoiceNo,
    createdAt = createdAt.parseLocalDateTimeOrNull(),
)

internal fun Ky10Dto.toDomain(): Ky10Entry = Ky10Entry(
    id = id,
    saleId = saleId,
    date = date.parseLocalDateOrNull(),
    drugName = drugName,
    regNo = regNo,
    qty = qty,
    unit = unit,
    buyerName = buyerName,
    buyerAddress = buyerAddress,
    rxNo = rxNo,
    doctor = doctor,
    balance = balance,
    createdAt = createdAt.parseLocalDateTimeOrNull(),
)

internal fun Ky11Dto.toDomain(): Ky11Entry = Ky11Entry(
    id = id,
    saleId = saleId,
    date = date.parseLocalDateOrNull(),
    drugName = drugName,
    regNo = regNo,
    qty = qty,
    unit = unit,
    buyerName = buyerName,
    purpose = purpose,
    pharmacist = pharmacist,
    createdAt = createdAt.parseLocalDateTimeOrNull(),
)

internal fun Ky12Dto.toDomain(): Ky12Entry = Ky12Entry(
    id = id,
    saleId = saleId,
    date = date.parseLocalDateOrNull(),
    rxNo = rxNo,
    patientName = patientName,
    doctor = doctor,
    hospital = hospital,
    drugName = drugName,
    qty = qty,
    unit = unit,
    totalValue = totalValue,
    status = status,
    createdAt = createdAt.parseLocalDateTimeOrNull(),
)
