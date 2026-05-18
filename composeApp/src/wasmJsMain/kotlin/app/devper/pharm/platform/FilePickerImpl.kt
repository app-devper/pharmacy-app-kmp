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
            input.onchange = { _ ->
                val file = input.files?.item(0)
                if (file == null) {
                    cont.resume(null)
                    document.body?.removeChild(input)
                } else {
                    val reader = FileReader()
                    reader.onload = { _ ->
                        val text = reader.result?.toString()
                        cont.resume(text)
                        document.body?.removeChild(input)
                    }
                    reader.onerror = { _ ->
                        cont.resume(null)
                        document.body?.removeChild(input)
                    }
                    reader.readAsText(file)
                }
            }
            input.oncancel = { _ ->
                cont.resume(null)
                document.body?.removeChild(input)
            }
            document.body?.appendChild(input)
            input.click()
        }
    }.recoverCatching { e ->
        if (e is AppException) throw e
        throw StorageException("เปิดไฟล์ไม่สำเร็จในเบราว์เซอร์", cause = e)
    }
}
