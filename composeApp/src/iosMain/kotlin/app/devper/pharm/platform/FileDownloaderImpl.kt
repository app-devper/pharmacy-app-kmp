package app.devper.pharm.platform

import app.devper.pharm.common.platform.FileDownloader
import platform.Foundation.NSLog

class FileDownloaderImpl : FileDownloader {
    override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> {
        NSLog("FileDownloader: save() not implemented on iOS (would have saved $filename, mime=$mimeType, ${bytes.size} bytes)")
        return Result.failure(
            UnsupportedOperationException("ยังไม่รองรับการดาวน์โหลดไฟล์บน iOS — ใช้เว็บแอดมินไปก่อน"),
        )
    }
}
