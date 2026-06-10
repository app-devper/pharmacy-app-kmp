package app.devper.pharm.presentation.bulkimport

import app.devper.pharm.common.AppException
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.domain.validation.parseBulkImportJson
import app.devper.pharm.domain.usecase.purchasing.BulkImportDrugsUseCase
import app.devper.pharm.presentation.bulkimport.exception.BulkImportUiStateError
import app.devper.pharm.ui.common.BaseLoadableViewModel

class BulkImportViewModel(
    private val bulkImportDrugs: BulkImportDrugsUseCase,
    private val filePicker: FilePicker,
) : BaseLoadableViewModel<BulkImportUiState>(BulkImportUiState()) {

    fun onTextChange(value: String) = setState {
        copy(
            text = value,
            parsed = emptyList(),
            previewCount = null,
            parseErrorState = null,
            result = null,
        )
    }

    fun pickFile() {
        launchResult<String?>(
            block = { filePicker.pickJsonFile() },
            onSuccess = { content ->
                if (content != null) {
                    onTextChange(content)
                    preview()
                }
            },
            onFailure = { e ->
                setState { copy(errorState = BulkImportUiStateError.PickFileFailed(e)) }
            },
        )
    }

    fun preview() {
        parseBulkImportJson(current.text).fold(
            onSuccess = { list ->
                setState {
                    copy(parsed = list, previewCount = list.size, parseErrorState = null, result = null)
                }
            },
            onFailure = { e ->
                setState { copy(parsed = emptyList(), previewCount = null, parseErrorState = (e as? AppException) ?: BulkImportUiStateError.InvalidJson(e)) }
            },
        )
    }

    fun submit() {
        val parsed = parseBulkImportJson(current.text).getOrElse { e ->
            setState { copy(parsed = emptyList(), parseErrorState = (e as? AppException) ?: BulkImportUiStateError.InvalidJson(e)) }
            return
        }
        if (parsed.isEmpty()) {
            setState { copy(parsed = emptyList(), parseErrorState = BulkImportUiStateError.NoRows()) }
            return
        }
        setState { copy(parsed = parsed, previewCount = parsed.size, result = null) }
        setState { copy(submitting = true, errorState = null) }
        launchResult(
            block = { bulkImportDrugs(parsed) },
            onSuccess = { res -> setState { copy(submitting = false, result = res) } },
            onFailure = { e -> setState { copy(submitting = false, errorState = BulkImportUiStateError.ImportFailed(e)) } },
        )
    }

    fun reset() = setState { BulkImportUiState() }
}
