package app.devper.pharm.platform

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import app.devper.pharm.common.AppException
import app.devper.pharm.common.StorageException
import app.devper.pharm.common.platform.FileDownloader
import java.io.File
import java.io.FileOutputStream

class FileDownloaderImpl(private val context: Context) : FileDownloader {
    override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> = runCatching {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            saveViaMediaStore(filename, mimeType, bytes)
        } else {
            saveToLegacyDownloads(filename, bytes)
        }
    }.recoverCatching { e ->
        if (e is AppException) throw e
        throw StorageException("ไม่สามารถบันทึก $filename", cause = e)
    }

    @androidx.annotation.RequiresApi(Build.VERSION_CODES.Q)
    private fun saveViaMediaStore(filename: String, mimeType: String, bytes: ByteArray): String {
        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val uri = resolver.insert(collection, values)
            ?: throw StorageException("ไม่สามารถสร้างไฟล์ใน Downloads")
        resolver.openOutputStream(uri)?.use { it.write(bytes) }
            ?: throw StorageException("ไม่สามารถเขียนไฟล์ใน Downloads")
        val finalValues = ContentValues().apply { put(MediaStore.Downloads.IS_PENDING, 0) }
        resolver.update(uri, finalValues, null, null)
        return "บันทึก $filename ลง Downloads แล้ว"
    }

    private fun saveToLegacyDownloads(filename: String, bytes: ByteArray): String {
        @Suppress("DEPRECATION")
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        if (!downloadsDir.exists()) downloadsDir.mkdirs()
        val target = nextAvailable(downloadsDir, filename)
        FileOutputStream(target).use { it.write(bytes) }
        return "บันทึก ${target.name} ลง Downloads แล้ว"
    }

    private fun nextAvailable(baseDir: File, filename: String): File {
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
