package app.devper.pharm.common.platform

interface FileDownloader {
    suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String>
}

object MimeType {
    const val Pdf: String = "application/pdf"
    const val Csv: String = "text/csv"
    const val Xlsx: String = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    const val Json: String = "application/json"
    const val PlainText: String = "text/plain"
}
