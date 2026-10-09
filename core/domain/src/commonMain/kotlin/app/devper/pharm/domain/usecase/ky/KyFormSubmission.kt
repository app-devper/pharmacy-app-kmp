package app.devper.pharm.domain.usecase.ky

import app.devper.pharm.domain.extension.isTemporaryDeliveryFailure
import app.devper.pharm.domain.model.KyForm
import app.devper.pharm.domain.repository.ky.KyRepository
import kotlinx.coroutines.CancellationException

fun KyForm.withSaleId(saleId: String): KyForm = when (this) {
    is KyForm.Ky10 -> copy(saleId = saleId)
    is KyForm.Ky11 -> copy(saleId = saleId)
    is KyForm.Ky12 -> copy(saleId = saleId)
}

data class KyFormsSent(
    val refused: List<Pair<KyForm, String>>,
    val unsent: List<KyForm>,
    val outage: Throwable?,
) {
    val remaining: List<KyForm> get() = refused.map { it.first } + unsent
    val refusedLabels: List<String> get() = refused.map { it.second }
}

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
            if (e.isTemporaryDeliveryFailure()) return KyFormsSent(refused, forms.drop(index), e)
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
