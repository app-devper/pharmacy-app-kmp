package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.ky.ExportRepository

import app.devper.pharm.domain.param.ky.ExportKyFormParam

class FakeExportRepository(
    private val result: String = "saved",
    private val throws: Boolean = false,
) : ExportRepository {

    var lastFilename: String? = null
        private set
    var lastBytes: ByteArray? = null
        private set
    var lastKyParam: ExportKyFormParam? = null
        private set

    override suspend fun exportKyForm(param: ExportKyFormParam): String {
        lastKyParam = param
        if (throws) throw RuntimeException("export failed")
        return result
    }

    override suspend fun saveCsv(filename: String, bytes: ByteArray): String {
        lastFilename = filename
        lastBytes = bytes
        if (throws) throw RuntimeException("export failed")
        return result
    }
}
