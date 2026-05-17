package app.devper.pharm.data.repository

import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.MimeType
import app.devper.pharm.data.remote.api.LabelApi
import app.devper.pharm.data.remote.dto.LabelLineRequest
import app.devper.pharm.data.remote.dto.PrintLabelsRequest
import app.devper.pharm.domain.model.LabelLine
import app.devper.pharm.domain.param.PrintLabelsParam
import app.devper.pharm.domain.repository.LabelRepository
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class LabelRepositoryImpl(
    private val api: LabelApi,
    private val downloader: FileDownloader,
) : LabelRepository {

    override suspend fun printLabels(param: PrintLabelsParam): String {
        val request = PrintLabelsRequest(
            size = param.size.wire,
            lines = param.lines.map { it.toDto() },
        )
        val bytes = api.printLabels(request)
        val filename = "labels_${Clock.System.now().toEpochMilliseconds()}.pdf"
        return downloader.save(filename, MimeType.Pdf, bytes).getOrThrow()
    }

    private fun LabelLine.toDto(): LabelLineRequest = LabelLineRequest(
        drugName = drugName,
        lotNumber = lotNumber,
        barcode = barcode,
        price = price,
        includePrice = includePrice,
        copies = copies,
    )
}
