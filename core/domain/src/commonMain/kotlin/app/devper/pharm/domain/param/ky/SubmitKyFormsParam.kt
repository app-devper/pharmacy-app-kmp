package app.devper.pharm.domain.param

import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.Sale

data class SubmitKyFormsParam(
    val sale: Sale,
    val required: KyRequired,
    val captured: KyCaptureFields,
    val dateYmd: String,
)
