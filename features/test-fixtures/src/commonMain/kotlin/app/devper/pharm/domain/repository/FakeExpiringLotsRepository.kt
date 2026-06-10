package app.devper.pharm.domain.repository

import app.devper.pharm.domain.repository.inventory.ExpiringLotsRepository

import app.devper.pharm.domain.model.ExpiringLot
import app.devper.pharm.domain.model.WriteoffResult
import app.devper.pharm.domain.param.inventory.ExpiringLotsFilterParam
import app.devper.pharm.domain.param.inventory.WriteoffLotsParam

class FakeExpiringLotsRepository(
    private val seed: List<ExpiringLot> = emptyList(),
    private val writeoffResult: WriteoffResult = WriteoffResult(writtenOff = 0, failures = emptyList()),
    private val listThrows: Boolean = false,
    private val writeoffThrows: Boolean = false,
) : ExpiringLotsRepository {

    var lastFilter: ExpiringLotsFilterParam? = null
        private set
    var lastWriteoff: WriteoffLotsParam? = null
        private set

    override suspend fun list(filter: ExpiringLotsFilterParam): List<ExpiringLot> {
        lastFilter = filter
        if (listThrows) throw RuntimeException("list failed")
        return seed
    }

    override suspend fun writeoff(param: WriteoffLotsParam): WriteoffResult {
        lastWriteoff = param
        if (writeoffThrows) throw RuntimeException("writeoff failed")
        return writeoffResult
    }
}
