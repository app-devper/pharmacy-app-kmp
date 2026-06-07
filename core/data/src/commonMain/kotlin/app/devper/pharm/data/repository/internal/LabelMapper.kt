package app.devper.pharm.data.repository.internal

import app.devper.pharm.data.remote.dto.LabelLineRequest
import app.devper.pharm.domain.model.LabelLine

internal fun LabelLine.toDto(): LabelLineRequest = LabelLineRequest(
    drugName = drugName,
    lotNumber = lotNumber,
    barcode = barcode,
    price = price,
    includePrice = includePrice,
    copies = copies,
)
