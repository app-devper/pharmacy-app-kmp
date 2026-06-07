package app.devper.pharm.presentation.bulkimport

import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.common.userMessageOr
import app.devper.pharm.domain.extension.parseBulkImportJson
import app.devper.pharm.domain.usecase.BulkImportDrugsUseCase
import app.devper.pharm.ui.common.BaseLoadableViewModel

private const val PICK_FILE_FAILED = "เลือกไฟล์ไม่สำเร็จ"
private const val IMPORT_FAILED = "นำเข้าไม่สำเร็จ"
private const val NO_ROWS = "ไม่มีรายการให้นำเข้า"

class BulkImportViewModel(
    private val bulkImportDrugs: BulkImportDrugsUseCase,
    private val filePicker: FilePicker,
) : BaseLoadableViewModel<BulkImportUiState>(BulkImportUiState()) {

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
                setState { copy(error = e.userMessageOr(PICK_FILE_FAILED)) }
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
            setState { copy(parsed = emptyList(), parseError = NO_ROWS) }
            return
        }
        setState { copy(parsed = parsed, previewCount = parsed.size, result = null) }
        launchLoad(
            block = { bulkImportDrugs(parsed) },
            fallback = IMPORT_FAILED,
            onSuccess = { res -> copy(result = res) },
        )
    }

    fun reset() = setState { BulkImportUiState() }
}
