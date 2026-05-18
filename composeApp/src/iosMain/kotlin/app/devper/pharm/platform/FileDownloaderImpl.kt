@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class, kotlinx.cinterop.BetaInteropApi::class)

package app.devper.pharm.platform

import app.devper.pharm.common.AppException
import app.devper.pharm.common.Logger
import app.devper.pharm.common.StorageException
import app.devper.pharm.common.platform.FileDownloader
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSData
import platform.Foundation.NSTemporaryDirectory
import platform.Foundation.NSURL
import platform.Foundation.create
import platform.Foundation.writeToURL
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIModalPresentationFormSheet
import platform.UIKit.UIViewController
import platform.UIKit.UIWindow
import platform.UIKit.UIUserInterfaceIdiomPad
import platform.UIKit.UIDevice

class FileDownloaderImpl(private val logger: Logger) : FileDownloader {

    override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> = runCatching {
        val safeName = sanitizeFilename(filename)
        val fileURL = writeToTemp(safeName, bytes)
        withContext(Dispatchers.Main) {
            presentShareSheet(fileURL)
        }
        "เปิด Share Sheet สำหรับ $safeName แล้ว — เลือกบันทึกหรือส่งต่อ"
    }.recoverCatching { e ->
        if (e is AppException) throw e
        logger.warn(tag = "FileDownloader", message = "iOS save failed for $filename", cause = e)
        throw StorageException("ไม่สามารถบันทึก $filename", cause = e)
    }

    private fun writeToTemp(filename: String, bytes: ByteArray): NSURL {
        val path = NSTemporaryDirectory() + filename
        val url = NSURL.fileURLWithPath(path)
        val data = bytes.toNSData()
        val ok = data.writeToURL(url, atomically = true)
        if (!ok) throw StorageException("เขียนไฟล์ชั่วคราวไม่สำเร็จ ($filename)")
        return url
    }

    private fun ByteArray.toNSData(): NSData = if (isEmpty()) {
        NSData()
    } else {
        usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = size.toULong())
        }
    }

    private fun presentShareSheet(fileURL: NSURL) {
        val activityVC = UIActivityViewController(
            activityItems = listOf(fileURL),
            applicationActivities = null,
        )
        if (isIpad()) {
            activityVC.modalPresentationStyle = UIModalPresentationFormSheet
        }
        val topVC = topmostViewController()
            ?: throw StorageException("ไม่พบหน้าจอที่จะแสดง Share Sheet")
        topVC.presentViewController(activityVC, animated = true, completion = null)
    }

    private fun isIpad(): Boolean =
        UIDevice.currentDevice.userInterfaceIdiom == UIUserInterfaceIdiomPad

    private fun topmostViewController(): UIViewController? {
        val rootVC = activeKeyWindow()?.rootViewController ?: return null
        var current: UIViewController? = rootVC
        while (current?.presentedViewController != null) {
            current = current.presentedViewController
        }
        return current
    }

    @Suppress("DEPRECATION")
    private fun activeKeyWindow(): UIWindow? {
        val app = UIApplication.sharedApplication
        app.keyWindow?.let { return it }
        @Suppress("UNCHECKED_CAST")
        val windows = app.windows as List<UIWindow>
        return windows.firstOrNull { it.isKeyWindow() } ?: windows.firstOrNull()
    }

    private fun sanitizeFilename(raw: String): String {
        val cleaned = raw.map { ch ->
            if (ch == '/' || ch == '\\' || ch.code < 0x20) '_' else ch
        }.joinToString("")
        return cleaned.ifBlank { "download" }
    }
}
