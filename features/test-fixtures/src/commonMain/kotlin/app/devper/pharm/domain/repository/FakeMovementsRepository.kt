package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.StockMovementsPage
import app.devper.pharm.domain.param.MovementsFilterParam

class FakeMovementsRepository(
    private val page: StockMovementsPage = StockMovementsPage(items = emptyList(), total = 0),
    private val throws: Boolean = false,
) : MovementsRepository {

    var lastFilter: MovementsFilterParam? = null
        private set

    override suspend fun list(filter: MovementsFilterParam): StockMovementsPage {
        lastFilter = filter
        if (throws) throw RuntimeException("movements failed")
        return page
    }
}
