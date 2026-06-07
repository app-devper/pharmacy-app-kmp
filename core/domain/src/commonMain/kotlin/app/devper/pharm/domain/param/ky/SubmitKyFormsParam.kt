package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.Sale
import kotlinx.datetime.LocalDate

data class SubmitKyFormsParam(
    val sale: Sale,
    val required: KyRequired,
    val captured: KyCaptureFields,
    val date: LocalDate,
)
