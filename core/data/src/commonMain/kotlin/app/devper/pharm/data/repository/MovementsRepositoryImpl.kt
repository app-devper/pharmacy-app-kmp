package app.devper.pharm.data.repository

import app.devper.pharm.data.internal.toIso
import app.devper.pharm.data.remote.api.MovementsApi
import app.devper.pharm.data.repository.internal.toDomainOrNull
import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.param.reports.MovementsFilterParam
import app.devper.pharm.domain.repository.reports.MovementsRepository

class MovementsRepositoryImpl(
    private val api: MovementsApi,
) : MovementsRepository {

    override suspend fun list(filter: MovementsFilterParam): StockMovementsPage {
        val dto = api.list(
            from = filter.from?.toIso(),
            to = filter.to?.toIso(),
            drugName = filter.drugName,
            types = filter.types.map { it.wire },
            limit = filter.limit,
            offset = filter.offset,
        )
        return StockMovementsPage(
            items = dto.items.mapNotNull { it.toDomainOrNull() },
            total = dto.total,
        )
    }
}
