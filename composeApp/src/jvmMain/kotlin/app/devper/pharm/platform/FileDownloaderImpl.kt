package app.devper.pharm.platform

import app.devper.pharm.common.AppException
import app.devper.pharm.common.Logger
import app.devper.pharm.common.StorageException
import app.devper.pharm.common.platform.FileDownloader
import java.awt.Desktop
import java.io.File

class FileDownloaderImpl(private val logger: Logger) : FileDownloader {
    override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> = runCatching {
        val target = resolveTarget(filename)
        target.writeBytes(bytes)
        tryAutoOpen(target)
        "บันทึกที่ ${target.absolutePath}"
    }.recoverCatching { e ->
        if (e is AppException) throw e
        throw StorageException("ไม่สามารถบันทึก $filename", cause = e)
    }

    private fun tryAutoOpen(target: File) {
        if (!Desktop.isDesktopSupported()) return
        runCatching { Desktop.getDesktop().open(target) }
            .onFailure { logger.warn("FileDownloader", "auto-open failed for ${target.name}", it) }
    }

    private fun resolveTarget(filename: String): File {
        val home = System.getProperty("user.home").orEmpty()
        val downloads = File(home, "Downloads")
        val baseDir = if (downloads.isDirectory) downloads else File(home).takeIf { it.isDirectory } ?: File(".")

        var candidate = File(baseDir, filename)
        var n = 1
        val stem = filename.substringBeforeLast('.', filename)
        val ext = filename.substringAfterLast('.', "bin")
        while (candidate.exists()) {
            candidate = File(baseDir, "$stem-$n.$ext")
            n++
        }
        return candidate
    }
}
