package app.devper.pharm.data.repository.internal

import app.devper.pharm.common.value.Money
import app.devper.pharm.data.internal.parseLocalDateTimeOrNull
import app.devper.pharm.data.remote.dto.SaleSummaryDto
import app.devper.pharm.domain.model.SaleSummary

internal fun SaleSummaryDto.toDomain(): SaleSummary = SaleSummary(
    id = id,
    billNo = billNo ?: "",
    customerName = customerName,
    total = Money(total),
    discount = Money(discount),
    soldAt = soldAt.parseLocalDateTimeOrNull(),
    voided = voided,
)
