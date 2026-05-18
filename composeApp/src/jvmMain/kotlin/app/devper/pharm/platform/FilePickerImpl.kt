package app.devper.pharm.platform

import app.devper.pharm.common.AppException
import app.devper.pharm.common.StorageException
import app.devper.pharm.common.platform.FilePicker
import java.awt.FileDialog
import java.awt.Frame
import java.io.File

class FilePickerImpl : FilePicker {
    override suspend fun pickJsonFile(): Result<String?> = runCatching {
        val dialog = FileDialog(null as Frame?, "เลือกไฟล์ JSON", FileDialog.LOAD).apply {
            isMultipleMode = false
            file = "*.json"
            setFilenameFilter { _, name -> name.endsWith(".json", ignoreCase = true) }
        }
        dialog.isVisible = true
        val name = dialog.file ?: return@runCatching null
        val dir = dialog.directory ?: ""
        val target = File(dir, name)
        if (!target.exists() || !target.isFile) {
            throw StorageException("ไม่พบไฟล์ที่เลือก: ${target.absolutePath}")
        }
        target.readText(Charsets.UTF_8)
    }.recoverCatching { e ->
        if (e is AppException) throw e
        throw StorageException("เปิดไฟล์ไม่สำเร็จ", cause = e)
    }
}
