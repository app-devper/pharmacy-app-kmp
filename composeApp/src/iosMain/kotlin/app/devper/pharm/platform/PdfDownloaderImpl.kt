package app.devper.pharm.platform

import app.devper.pharm.common.platform.PdfDownloader
import platform.Foundation.NSLog

class PdfDownloaderImpl : PdfDownloader {
    override suspend fun save(filename: String, bytes: ByteArray): Result<String> {
        NSLog("PdfDownloader: save() not implemented on iOS (would have saved $filename, ${bytes.size} bytes)")
        return Result.failure(
            UnsupportedOperationException("ยังไม่รองรับการดาวน์โหลด PDF บน iOS — ใช้เว็บแอดมินไปก่อน"),
        )
    }
}
