package app.devper.pharm.domain.repository

import app.devper.pharm.domain.param.PrintLabelsParam

class FakeLabelRepository(
    private val throws: Boolean = false,
    private val saveAs: String = "saved labels.pdf",
) : LabelRepository {

    var lastParam: PrintLabelsParam? = null
        private set
    var callCount: Int = 0
        private set

    override suspend fun printLabels(param: PrintLabelsParam): String {
        callCount++
        if (throws) throw RuntimeException("printer offline")
        lastParam = param
        return saveAs
    }
}
