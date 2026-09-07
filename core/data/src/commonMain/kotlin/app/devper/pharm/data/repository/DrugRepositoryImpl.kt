package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.DrugApi
import app.devper.pharm.data.remote.dto.BulkDrugImportInputDto
import app.devper.pharm.data.repository.internal.toDomain
import app.devper.pharm.data.repository.internal.toRequest
import app.devper.pharm.domain.event.StockChangeBus
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.inventory.AddDrugParam
import app.devper.pharm.domain.param.inventory.ReorderSuggestionsParam
import app.devper.pharm.domain.param.inventory.UpdateDrugParam
import app.devper.pharm.domain.repository.inventory.DrugRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeSource

private val LIST_CACHE_TTL = 30.seconds

class DrugRepositoryImpl(
    private val api: DrugApi,
    private val stockChangeBus: StockChangeBus,
) : DrugRepository {

    private val listCacheLock = Mutex()
    private var cachedList: List<Drug>? = null
    private var cachedGeneration: Int = -1
    private var cachedAt: TimeSource.Monotonic.ValueTimeMark? = null

    override suspend fun list(): List<Drug> = listCacheLock.withLock {
        val generation = stockChangeBus.generation.value
        val cached = cachedList
        val age = cachedAt?.elapsedNow()
        if (cached != null && cachedGeneration == generation && age != null && age < LIST_CACHE_TTL) {
            return@withLock cached
        }
        val fresh = api.list().map { it.toDomain() }
        cachedList = fresh
        cachedGeneration = generation
        cachedAt = TimeSource.Monotonic.markNow()
        fresh
    }

    override suspend fun add(param: AddDrugParam): Drug {
        val drug = api.add(param.toRequest()).toDomain()
        stockChangeBus.emit()
        return drug
    }

    override suspend fun update(param: UpdateDrugParam) {
        api.update(param.id, param.toRequest())
        stockChangeBus.emit()
    }

    override suspend fun bulkImport(drugs: List<AddDrugParam>): BulkImportResult {
        val response = api.bulkImport(BulkDrugImportInputDto(drugs.map { it.toRequest() }))

        if (response.imported > 0) stockChangeBus.emit()
        return response.toDomain()
    }

    override suspend fun lowStock(): List<Drug> = api.lowStock().map { it.toDomain() }

    override suspend fun reorderSuggestions(param: ReorderSuggestionsParam): List<ReorderSuggestion> =
        api.reorderSuggestions(param.days, param.lookahead).map { it.toDomain() }
}
