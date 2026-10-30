package app.devper.pharm.domain.repository.export

import app.devper.pharm.domain.param.ky.ExportKyFormParam

interface ExportRepository {

    suspend fun exportKyForm(param: ExportKyFormParam): String

    suspend fun saveCsv(filename: String, bytes: ByteArray): String
}
