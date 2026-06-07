package app.devper.pharm.presentation.ky

import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.i18n.PharmStrings

internal val KyFormType.number: Int
    get() = when (this) {
        KyFormType.Ky9 -> 9
        KyFormType.Ky10 -> 10
        KyFormType.Ky11 -> 11
        KyFormType.Ky12 -> 12
    }

internal data class KyFormMeta(
    val title: String,
    val subtitle: String,
)

internal fun kyFormMeta(form: KyFormType, s: PharmStrings): KyFormMeta = when (form) {
    KyFormType.Ky9 -> KyFormMeta(title = s.kyForm9Title, subtitle = s.kyFormFullName9)
    KyFormType.Ky10 -> KyFormMeta(title = s.kyForm10Title, subtitle = s.kyFormFullName10)
    KyFormType.Ky11 -> KyFormMeta(title = s.kyForm11Title, subtitle = s.kyFormFullName11)
    KyFormType.Ky12 -> KyFormMeta(title = s.kyForm12Title, subtitle = s.kyFormFullName12)
}
