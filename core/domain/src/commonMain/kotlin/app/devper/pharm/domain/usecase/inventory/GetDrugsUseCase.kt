package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.repository.DrugRepository

class GetDrugsUseCase(private val drugs: DrugRepository, dispatchers: AppDispatchers) : BaseUseCase<Unit, List<Drug>>(dispatchers) {
    override suspend fun execute(param: Unit): List<Drug> = drugs.list()
    suspend operator fun invoke(): Result<List<Drug>> = invoke(Unit)
}
