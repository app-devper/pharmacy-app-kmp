package app.devper.pharm.platform

import app.devper.pharm.common.Logger
import app.devper.pharm.common.UnsupportedPlatformException
import app.devper.pharm.common.platform.FileDownloader

class FileDownloaderImpl(private val logger: Logger) : FileDownloader {
    override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> {
        logger.warn(
            tag = "FileDownloader",
            message = "iOS stub: $filename ($mimeType, ${bytes.size} bytes) — not saved",
        )
        return Result.failure(
            UnsupportedPlatformException("ยังไม่รองรับการดาวน์โหลดไฟล์บน iOS — ใช้เว็บแอดมินไปก่อน"),
        )
    }
}
