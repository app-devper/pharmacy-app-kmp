package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.extension.looksLikeTemporaryOutage
import app.devper.pharm.domain.model.KyCaptureFields
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.KyRequired
import app.devper.pharm.domain.repository.ky.KyRepository
import kotlinx.coroutines.CancellationException
import kotlinx.datetime.LocalDate

/**
 * The KY forms a bill needs, built from the lines that require them and what
 * the cashier captured. [saleId] is blank while the bill is not yet confirmed;
 * [withSaleId] attaches the confirmed id before the forms are sent.
 */
fun KyRequired.toForms(saleId: String, captured: KyCaptureFields, date: LocalDate): List<KyForm> =
    ky10.map { line ->
        KyForm.Ky10(
            saleId = saleId,
            date = date,
            drugName = line.drug.name,
            regNo = line.drug.regNo.orEmpty(),
            qty = line.qty,
            unit = line.drug.unit.orEmpty(),
            buyerName = captured.ky10BuyerName,
            buyerAddress = captured.ky10BuyerAddress,
            rxNo = captured.ky10RxNo,
            doctor = captured.ky10Doctor,
            balance = captured.ky10Balance,
        )
    } + ky11.map { line ->
        KyForm.Ky11(
            saleId = saleId,
            date = date,
            drugName = line.drug.name,
            regNo = line.drug.regNo.orEmpty(),
            qty = line.qty,
            unit = line.drug.unit.orEmpty(),
            buyerName = captured.ky11BuyerName,
            purpose = captured.ky11Purpose,
            pharmacist = captured.ky11Pharmacist,
        )
    } + ky12.map { line ->
        KyForm.Ky12(
            saleId = saleId,
            date = date,
            drugName = line.drug.name,
            regNo = line.drug.regNo.orEmpty(),
            qty = line.qty,
            unit = line.drug.unit.orEmpty(),
            rxNo = captured.ky12RxNo,
            patientName = captured.ky12PatientName,
            doctor = captured.ky12Doctor,
            hospital = captured.ky12Hospital,
            totalValue = (line.unitPrice * line.displayQty).amount,
            status = captured.ky12Status,
        )
    }

fun KyForm.withSaleId(saleId: String): KyForm = when (this) {
    is KyForm.Ky10 -> copy(saleId = saleId)
    is KyForm.Ky11 -> copy(saleId = saleId)
    is KyForm.Ky12 -> copy(saleId = saleId)
}

/** Result of sending a bill's KY forms. */
data class KyFormsSent(
    /** Forms the server refused, with a `kyNN:drug:reason` label each. */
    val refused: List<Pair<KyForm, String>>,
    /** Forms not sent because the network or identity check went down. */
    val unsent: List<KyForm>,
    /** The temporary outage that stopped sending, if any. */
    val outage: Throwable?,
) {
    /** Forms still to record: refused first, then unsent. */
    val remaining: List<KyForm> get() = refused.map { it.first } + unsent
    val refusedLabels: List<String> get() = refused.map { it.second }
}

/** Send [forms] in order, stopping at the first temporary outage. */
suspend fun KyRepository.submitForms(forms: List<KyForm>): KyFormsSent {
    val refused = mutableListOf<Pair<KyForm, String>>()
    forms.forEachIndexed { index, form ->
        try {
            when (form) {
                is KyForm.Ky10 -> submitKy10(form)
                is KyForm.Ky11 -> submitKy11(form)
                is KyForm.Ky12 -> submitKy12(form)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (e.looksLikeTemporaryOutage()) return KyFormsSent(refused, forms.drop(index), e)
            refused += form to "${form.label}:${form.drugName}:${e.message.orEmpty()}"
        }
    }
    return KyFormsSent(refused, emptyList(), null)
}

internal val KyForm.label: String
    get() = when (this) {
        is KyForm.Ky10 -> "ky10"
        is KyForm.Ky11 -> "ky11"
        is KyForm.Ky12 -> "ky12"
    }
