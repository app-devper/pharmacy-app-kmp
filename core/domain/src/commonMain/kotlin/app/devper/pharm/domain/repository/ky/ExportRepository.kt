package app.devper.pharm.domain.repository

import app.devper.pharm.domain.param.ExportKyFormParam

interface ExportRepository {

    suspend fun exportKyForm(param: ExportKyFormParam): String
}
