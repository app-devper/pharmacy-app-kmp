package app.devper.pharm.domain.pendingsales

import app.devper.pharm.domain.extension.isTemporaryDeliveryFailure
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.domain.model.KyRefusal
import app.devper.pharm.domain.repository.ky.KyRepository
import kotlinx.coroutines.CancellationException

internal fun KyForm.withSaleId(saleId: String): KyForm = when (this) {
    is KyForm.Ky10 -> copy(saleId = saleId)
    is KyForm.Ky11 -> copy(saleId = saleId)
    is KyForm.Ky12 -> copy(saleId = saleId)
}

internal val KyForm.type: KyFormType
    get() = when (this) {
        is KyForm.Ky10 -> KyFormType.Ky10
        is KyForm.Ky11 -> KyFormType.Ky11
        is KyForm.Ky12 -> KyFormType.Ky12
    }

internal data class KyFormsSent(
    val refused: List<Pair<KyForm, KyRefusal>>,
    val unsent: List<KyForm>,
    val outage: Throwable?,
) {
    val remaining: List<KyForm> get() = refused.map { it.first } + unsent
    val refusals: List<KyRefusal> get() = refused.map { it.second }
}

internal suspend fun KyRepository.submitForms(forms: List<KyForm>): KyFormsSent {
    val refused = mutableListOf<Pair<KyForm, KyRefusal>>()
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
            if (e.isTemporaryDeliveryFailure()) return KyFormsSent(refused, forms.drop(index), e)
            refused += form to KyRefusal(form.type, form.drugName, e.serverReason())
        }
    }
    return KyFormsSent(refused, emptyList(), null)
}
