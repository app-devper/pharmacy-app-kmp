package app.devper.pharm.presentation.bulkimport

import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.domain.extension.parseBulkImportJson
import app.devper.pharm.domain.usecase.BulkImportDrugsUseCase
import app.devper.pharm.ui.common.BaseViewModel

class BulkImportViewModel(
    private val bulkImportDrugs: BulkImportDrugsUseCase,
    private val filePicker: FilePicker,
) : BaseViewModel<BulkImportUiState>(BulkImportUiState()) {

    fun onTextChange(value: String) = setState {
        copy(
            text = value,
            parsed = emptyList(),
            previewCount = null,
            parseError = null,
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
                setState { copy(error = e.message ?: "เลือกไฟล์ไม่สำเร็จ") }
            },
        )
    }

    fun preview() {
        parseBulkImportJson(current.text).fold(
            onSuccess = { list ->
                setState {
                    copy(parsed = list, previewCount = list.size, parseError = null, result = null)
                }
            },
            onFailure = { e ->
                setState { copy(parsed = emptyList(), previewCount = null, parseError = e.message) }
            },
        )
    }

    fun submit() {
        val parsed = parseBulkImportJson(current.text).getOrElse { e ->
            setState { copy(parsed = emptyList(), parseError = e.message) }
            return
        }
        if (parsed.isEmpty()) {
            setState { copy(parsed = emptyList(), parseError = "ไม่มีรายการให้นำเข้า") }
            return
        }
        setState {
            copy(parsed = parsed, previewCount = parsed.size, submitting = true, error = null, result = null)
        }
        launchResult(
            block = { bulkImportDrugs(parsed) },
            onSuccess = { res ->
                setState { copy(submitting = false, result = res) }
            },
            onFailure = { e ->
                setState { copy(submitting = false, error = e.message ?: "นำเข้าไม่สำเร็จ") }
            },
        )
    }

    fun dismissError() = setState { copy(error = null) }
    fun reset() = setState { BulkImportUiState() }
}
