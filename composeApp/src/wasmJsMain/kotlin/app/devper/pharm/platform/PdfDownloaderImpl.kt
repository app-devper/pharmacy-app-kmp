@file:OptIn(kotlin.io.encoding.ExperimentalEncodingApi::class)

package app.devper.pharm.platform

import app.devper.pharm.common.platform.PdfDownloader
import kotlinx.browser.document
import kotlin.io.encoding.Base64
import org.w3c.dom.HTMLAnchorElement

class PdfDownloaderImpl : PdfDownloader {
    override suspend fun save(filename: String, bytes: ByteArray): Result<String> = runCatching {
        val base64 = Base64.encode(bytes)
        val anchor = document.createElement("a") as HTMLAnchorElement
        anchor.href = "data:application/pdf;base64,$base64"
        anchor.download = filename
        anchor.style.display = "none"
        document.body?.appendChild(anchor)
        anchor.click()
        document.body?.removeChild(anchor)
        "เริ่มดาวน์โหลดในเบราว์เซอร์"
    }
}
