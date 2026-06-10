package app.devper.pharm.presentation.reports

import app.devper.pharm.common.print.ReceiptTemplate
import app.devper.pharm.domain.model.EodCloseResult
import app.devper.pharm.domain.model.EodReport
import app.devper.pharm.common.AppException
import app.devper.pharm.ui.common.LoadableUiState

data class EodUiState(
    val date: String = "",
    override val loading: Boolean = false,
    val report: EodReport? = null,
    val closed: Boolean = false,
    val closeResult: EodCloseResult? = null,
    val closedTemplate: ReceiptTemplate? = null,
    val closing: Boolean = false,
    val confirmClose: Boolean = false,
    val errorState: AppException? = null,
) : LoadableUiState<EodUiState> {

    override fun withLoading(value: Boolean) = copy(loading = value)
    override val domainError: AppException? get() = errorState
    override fun withDomainError(error: AppException?) = copy(errorState = error)
}
