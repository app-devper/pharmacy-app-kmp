package app.devper.pharm.platform

import app.devper.pharm.common.platform.FileDownloader
import java.awt.Desktop
import java.io.File

class FileDownloaderImpl : FileDownloader {
    override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> = runCatching {
        val target = resolveTarget(filename)
        target.writeBytes(bytes)
        runCatching {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(target)
            }
        }
        "บันทึกที่ ${target.absolutePath}"
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
