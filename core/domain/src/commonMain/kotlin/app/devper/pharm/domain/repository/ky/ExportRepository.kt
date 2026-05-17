package app.devper.pharm.domain.repository

import app.devper.pharm.domain.param.ExportKyFormParam

interface ExportRepository {

    suspend fun exportKyForm(param: ExportKyFormParam): String

    suspend fun saveCsv(filename: String, bytes: ByteArray): String
}
