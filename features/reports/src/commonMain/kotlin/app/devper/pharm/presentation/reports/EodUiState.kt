package app.devper.pharm.presentation.reports

import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState
import app.devper.pharm.ui.format.toLocalDateOrNull

data class EodUiState(
    val date: String = "",
    val validationRequested: Boolean = false,
    override val loading: Boolean = false,
    val report: EodReport? = null,
    val closed: Boolean = false,
    val closeResult: EodCloseResult? = null,
    val closedTemplate: ReceiptTemplate? = null,
    val closing: Boolean = false,
    val confirmClose: Boolean = false,
    val pendingSyncCount: Int = 0,
    val errorState: AppException? = null,
) : LoadableUiState<EodUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)

    val dateValid: Boolean = date.isBlank() || date.toLocalDateOrNull() != null
    val dateErrorVisible: Boolean = validationRequested && !dateValid
}
