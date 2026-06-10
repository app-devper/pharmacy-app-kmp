package app.devper.pharm.domain.repository.inventory

import app.devper.pharm.domain.param.labels.PrintLabelsParam

interface LabelRepository {
    suspend fun printLabels(param: PrintLabelsParam): String
}
