package app.devper.pharm.common.platform

interface PdfDownloader {
    suspend fun save(filename: String, bytes: ByteArray): Result<String>
}
