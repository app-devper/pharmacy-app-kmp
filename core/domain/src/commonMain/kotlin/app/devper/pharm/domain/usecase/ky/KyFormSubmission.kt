package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.extension.looksLikeTemporaryOutage
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.repository.ky.KyRepository
import kotlinx.coroutines.CancellationException

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
