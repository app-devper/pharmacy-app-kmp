package app.devper.pharm.domain.repository.reports

import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.param.reports.MovementsFilterParam

interface MovementsRepository {
    suspend fun list(filter: MovementsFilterParam): StockMovementsPage
}
