package app.devper.pharm.data.repository

import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.MimeType
import app.devper.pharm.data.remote.api.ExportApi
import app.devper.pharm.domain.param.ExportKyFormParam
import app.devper.pharm.domain.repository.ExportRepository

class ExportRepositoryImpl(
    private val api: ExportApi,
    private val downloader: FileDownloader,
) : ExportRepository {

    override suspend fun exportKyForm(param: ExportKyFormParam): String {
        val bytes = api.exportKyForm(param.form, param.month.trim())
        val filename = buildFilename(param.form, param.month)
        return downloader.save(filename, MimeType.Pdf, bytes).getOrThrow()
    }

    override suspend fun saveCsv(filename: String, bytes: ByteArray): String =
        downloader.save(filename, MimeType.Csv, bytes).getOrThrow()

    private fun buildFilename(form: String, month: String): String =
        if (month.isBlank()) "$form-all.pdf" else "$form-$month.pdf"
}
