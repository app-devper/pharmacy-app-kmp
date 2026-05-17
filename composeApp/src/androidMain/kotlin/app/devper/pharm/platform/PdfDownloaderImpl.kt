package app.devper.pharm.platform

import android.content.Context
import android.util.Log
import app.devper.pharm.common.platform.PdfDownloader

class PdfDownloaderImpl(@Suppress("unused") private val context: Context) : PdfDownloader {
    override suspend fun save(filename: String, bytes: ByteArray): Result<String> {
        Log.w("PdfDownloader", "save() not implemented on Android (would have saved $filename, ${bytes.size} bytes)")
        return Result.failure(
            UnsupportedOperationException("ยังไม่รองรับการดาวน์โหลด PDF บน Android — ใช้เว็บแอดมินไปก่อน"),
        )
    }
}
