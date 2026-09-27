package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.KySubmissionResult
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.ky.SubmitKyFormsParam
import app.devper.pharm.domain.repository.ky.KyRepository
import kotlinx.datetime.LocalDate

class SubmitKyFormsUseCase(private val ky: KyRepository, dispatchers: AppDispatchers) :
    BaseUseCase<SubmitKyFormsParam, KySubmissionResult>(dispatchers) {

    suspend operator fun invoke(
        sale: Sale,
        required: KyRequired,
        captured: KyCaptureFields,
        date: LocalDate,
    ): Result<KySubmissionResult> = invoke(SubmitKyFormsParam(sale, required, captured, date))

    override suspend fun execute(param: SubmitKyFormsParam): KySubmissionResult {
        val forms = param.required.toForms(param.sale.id, param.captured, param.date)
        val sent = ky.submitForms(forms)
        val unsent = sent.unsent.map { "${it.label}:${it.drugName}:${sent.outage?.message.orEmpty()}" }
        return KySubmissionResult(attempted = forms.size, failed = sent.refusedLabels + unsent)
    }
}
