package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.param.MovementsFilterParam

interface MovementsRepository {
    suspend fun list(filter: MovementsFilterParam): StockMovementsPage
}
