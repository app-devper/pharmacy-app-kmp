package app.devper.pharm.domain.repository

import app.devper.pharm.domain.param.PrintLabelsParam

interface LabelRepository {
    suspend fun printLabels(param: PrintLabelsParam): String
}
