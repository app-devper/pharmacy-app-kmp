package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyForm
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
        val errors = mutableListOf<String>()
        val saleId = param.sale.id

        for (line in param.required.ky10) {
            val form = KyForm.Ky10(
                saleId = saleId,
                date = param.date,
                drugName = line.drug.name,
                regNo = line.drug.regNo.orEmpty(),
                qty = line.qty,
                unit = line.drug.unit.orEmpty(),
                buyerName = param.captured.ky10BuyerName,
                buyerAddress = param.captured.ky10BuyerAddress,
                rxNo = param.captured.ky10RxNo,
                doctor = param.captured.ky10Doctor,
                balance = param.captured.ky10Balance,
            )
            try { ky.submitKy10(form) } catch (e: Exception) { errors += "ky10:${line.drug.name}:${e.message.orEmpty()}" }
        }

        for (line in param.required.ky11) {
            val form = KyForm.Ky11(
                saleId = saleId,
                date = param.date,
                drugName = line.drug.name,
                regNo = line.drug.regNo.orEmpty(),
                qty = line.qty,
                unit = line.drug.unit.orEmpty(),
                buyerName = param.captured.ky11BuyerName,
                purpose = param.captured.ky11Purpose,
                pharmacist = param.captured.ky11Pharmacist,
            )
            try { ky.submitKy11(form) } catch (e: Exception) { errors += "ky11:${line.drug.name}:${e.message.orEmpty()}" }
        }

        for (line in param.required.ky12) {
            val form = KyForm.Ky12(
                saleId = saleId,
                date = param.date,
                drugName = line.drug.name,
                regNo = line.drug.regNo.orEmpty(),
                qty = line.qty,
                unit = line.drug.unit.orEmpty(),
                rxNo = param.captured.ky12RxNo,
                patientName = param.captured.ky12PatientName,
                doctor = param.captured.ky12Doctor,
                hospital = param.captured.ky12Hospital,
                totalValue = (line.unitPrice * line.displayQty).amount,
                status = param.captured.ky12Status,
            )
            try { ky.submitKy12(form) } catch (e: Exception) { errors += "ky12:${line.drug.name}:${e.message.orEmpty()}" }
        }

        val attempted = param.required.ky10.size + param.required.ky11.size + param.required.ky12.size
        return KySubmissionResult(attempted = attempted, failed = errors)
    }
}
