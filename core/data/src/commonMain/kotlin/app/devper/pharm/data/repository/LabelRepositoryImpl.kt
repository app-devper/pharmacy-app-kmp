package app.devper.pharm.data.repository

import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.MimeType
import app.devper.pharm.data.remote.api.LabelApi
import app.devper.pharm.data.remote.dto.PrintLabelsRequest
import app.devper.pharm.data.repository.internal.toDto
import app.devper.pharm.domain.param.labels.PrintLabelsParam
import app.devper.pharm.domain.repository.inventory.LabelRepository
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
}
