package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.model.KySubmissionResult
import app.devper.pharm.domain.model.Sale
import app.devper.pharm.domain.param.SubmitKyFormsParam
import app.devper.pharm.domain.repository.KyRepository

class SubmitKyFormsUseCase(private val ky: KyRepository, dispatchers: AppDispatchers) :
    BaseUseCase<SubmitKyFormsParam, KySubmissionResult>(dispatchers) {

    suspend operator fun invoke(
        sale: Sale,
        required: KyRequired,
        captured: KyCaptureFields,
        dateYmd: String,
    ): Result<KySubmissionResult> = invoke(SubmitKyFormsParam(sale, required, captured, dateYmd))

    override suspend fun execute(param: SubmitKyFormsParam): KySubmissionResult {
        val errors = mutableListOf<String>()
        var attempted = 0
        val saleId = param.sale.id

        for (line in param.required.ky10) {
            attempted++
            val form = KyForm.Ky10(
                saleId = saleId,
                date = param.dateYmd,
                drugName = line.drug.name,
                regNo = line.drug.regNo.orEmpty(),
                qty = line.qty,
                unit = line.drug.unit ?: "หน่วย",
                buyerName = param.captured.ky10BuyerName,
                buyerAddress = param.captured.ky10BuyerAddress,
                rxNo = param.captured.ky10RxNo,
                doctor = param.captured.ky10Doctor,
                balance = param.captured.ky10Balance,
            )
            runCatching { ky.submitKy10(form) }
                .onFailure { errors += "ขย.10 ${line.drug.name}: ${it.message ?: "ไม่ทราบสาเหตุ"}" }
        }

        for (line in param.required.ky11) {
            attempted++
            val form = KyForm.Ky11(
                saleId = saleId,
                date = param.dateYmd,
                drugName = line.drug.name,
                regNo = line.drug.regNo.orEmpty(),
                qty = line.qty,
                unit = line.drug.unit ?: "หน่วย",
                buyerName = param.captured.ky11BuyerName,
                purpose = param.captured.ky11Purpose,
                pharmacist = param.captured.ky11Pharmacist,
            )
            runCatching { ky.submitKy11(form) }
                .onFailure { errors += "ขย.11 ${line.drug.name}: ${it.message ?: "ไม่ทราบสาเหตุ"}" }
        }

        for (line in param.required.ky12) {
            attempted++
            val form = KyForm.Ky12(
                saleId = saleId,
                date = param.dateYmd,
                drugName = line.drug.name,
                regNo = line.drug.regNo.orEmpty(),
                qty = line.qty,
                unit = line.drug.unit ?: "หน่วย",
                rxNo = param.captured.ky12RxNo,
                patientName = param.captured.ky12PatientName,
                doctor = param.captured.ky12Doctor,
                hospital = param.captured.ky12Hospital,
                totalValue = line.unitPrice * line.displayQty,
                status = param.captured.ky12Status,
            )
            runCatching { ky.submitKy12(form) }
                .onFailure { errors += "ขย.12 ${line.drug.name}: ${it.message ?: "ไม่ทราบสาเหตุ"}" }
        }

        return KySubmissionResult(attempted = attempted, failed = errors)
    }
}
