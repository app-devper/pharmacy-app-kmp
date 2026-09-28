package app.devper.pharm.domain.repository

import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.domain.model.PendingSale
import app.devper.pharm.domain.pendingsales.PendingSales
import app.devper.pharm.domain.repository.ky.KyRepository
import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue
import app.devper.pharm.domain.repository.sales.SaleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeOfflineSaleQueue(
    seed: List<PendingSale> = emptyList(),
    private val putThrows: Throwable? = null,
) : OfflineSaleQueue {

    private val state = MutableStateFlow(seed)
    override val entries: StateFlow<List<PendingSale>> = state.asStateFlow()

    /** The first stored version of each entry, in order. */
    val added = mutableListOf<PendingSale>()

    override fun put(entry: PendingSale) {
        putThrows?.let { throw it }
        if (state.value.none { it.id == entry.id }) added += entry
        state.value = state.value.filterNot { it.id == entry.id } + entry
    }

    override fun remove(id: String) {
        state.value = state.value.filterNot { it.id == id }
    }

    fun entry(id: String): PendingSale? = state.value.firstOrNull { it.id == id }
}

class FakeFileDownloader(private val fails: Throwable? = null) : FileDownloader {
    val saved = mutableListOf<Pair<String, String>>()

    override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> {
        fails?.let { return Result.failure(it) }
        saved += filename to bytes.decodeToString()
        return Result.success("/downloads/$filename")
    }
}

/** [PendingSales] over fakes. */
fun pendingSalesOf(
    queue: FakeOfflineSaleQueue = FakeOfflineSaleQueue(),
    sales: SaleRepository = FakeSaleRepository(),
    ky: KyRepository = FakeKyRepository(),
    files: FileDownloader = FakeFileDownloader(),
): PendingSales = PendingSales(queue, sales, ky, files)
