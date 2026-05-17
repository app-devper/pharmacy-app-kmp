package app.devper.pharm.data.repository

import app.devper.pharm.data.remote.api.MovementsApi
import app.devper.pharm.data.remote.dto.MovementDto
import app.devper.pharm.domain.model.MovementType
import app.devper.pharm.domain.model.StockMovement
import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.param.MovementsFilterParam
import app.devper.pharm.domain.repository.MovementsRepository

class MovementsRepositoryImpl(
    private val api: MovementsApi,
) : MovementsRepository {

    override suspend fun list(filter: MovementsFilterParam): StockMovementsPage {
        val dto = api.list(
            from = filter.from,
            to = filter.to,
            drugName = filter.drugName,
            types = filter.types.map { it.wire },
            limit = filter.limit,
            offset = filter.offset,
        )
        return StockMovementsPage(
            items = dto.items.mapNotNull(::toDomainOrNull),
            total = dto.total,
        )
    }

    private fun toDomainOrNull(d: MovementDto): StockMovement? {
        val type = MovementType.fromWire(d.type) ?: return null
        return StockMovement(
            id = d.id,
            type = type,
            drugId = d.drugId,
            drugName = d.drugName,
            delta = d.delta,
            reference = d.reference,
            note = d.note,
            at = d.at,
        )
    }
}
