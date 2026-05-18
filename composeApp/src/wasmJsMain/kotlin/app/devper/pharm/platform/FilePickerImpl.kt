package app.devper.pharm.platform

import app.devper.pharm.common.AppException
import app.devper.pharm.common.StorageException
import app.devper.pharm.common.platform.FilePicker
import kotlinx.browser.document
import kotlinx.coroutines.suspendCancellableCoroutine
import org.w3c.dom.HTMLInputElement
import org.w3c.files.FileReader
import kotlin.coroutines.resume

class FilePickerImpl : FilePicker {
    override suspend fun pickJsonFile(): Result<String?> = runCatching {
        suspendCancellableCoroutine<String?> { cont ->
            val input = document.createElement("input") as HTMLInputElement
            input.type = "file"
            input.accept = ".json,application/json"
            input.style.display = "none"

            fun cleanup() {
                if (input.parentNode != null) {
                    document.body?.removeChild(input)
                }
            }

            fun finish(value: String?) {
                cleanup()
                if (cont.isActive) cont.resume(value)
            }

            input.onchange = { _ ->
                val file = input.files?.item(0)
                if (file == null) {
                    finish(null)
                } else {
                    val reader = FileReader()
                    reader.onload = { _ -> finish(reader.result?.toString()) }
                    reader.onerror = { _ -> finish(null) }
                    reader.readAsText(file)
                }
            }
            input.oncancel = { _ -> finish(null) }

            cont.invokeOnCancellation { cleanup() }

            document.body?.appendChild(input)
            input.click()
        }
    }.recoverCatching { e ->
        if (e is AppException) throw e
        throw StorageException("เปิดไฟล์ไม่สำเร็จในเบราว์เซอร์", cause = e)
    }
}
