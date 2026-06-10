package app.devper.pharm.presentation.ky

import app.devper.pharm.common.AppException

import app.devper.pharm.domain.model.Ky10Entry
import app.devper.pharm.domain.model.Ky11Entry
import app.devper.pharm.domain.model.Ky12Entry
import app.devper.pharm.domain.model.Ky9Entry
import app.devper.pharm.domain.model.KyFormType
import app.devper.pharm.ui.common.LoadableUiState
import kotlinx.datetime.LocalDate

sealed interface KyRow {
    val id: String
    val date: LocalDate?
    val drugName: String

    data class Ky9(val entry: Ky9Entry) : KyRow {
        override val id: String get() = entry.id
        override val date: LocalDate? get() = entry.date
        override val drugName: String get() = entry.drugName
    }

    data class Ky10(val entry: Ky10Entry) : KyRow {
        override val id: String get() = entry.id
        override val date: LocalDate? get() = entry.date
        override val drugName: String get() = entry.drugName
    }

    data class Ky11(val entry: Ky11Entry) : KyRow {
        override val id: String get() = entry.id
        override val date: LocalDate? get() = entry.date
        override val drugName: String get() = entry.drugName
    }

    data class Ky12(val entry: Ky12Entry) : KyRow {
        override val id: String get() = entry.id
        override val date: LocalDate? get() = entry.date
        override val drugName: String get() = entry.drugName
    }
}

data class KyListUiState(
    val formType: KyFormType = KyFormType.Ky10,
    val month: String = "",
    override val loading: Boolean = false,
    val rows: List<KyRow> = emptyList(),
    val exporting: Boolean = false,
    val message: String? = null,
    val errorState: AppException? = null,
) : LoadableUiState<KyListUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
