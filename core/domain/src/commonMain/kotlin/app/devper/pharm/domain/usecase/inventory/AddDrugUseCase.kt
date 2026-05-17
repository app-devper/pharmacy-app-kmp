package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.domain.repository.DrugRepository

class AddDrugUseCase(private val drugs: DrugRepository, dispatchers: AppDispatchers) : BaseUseCase<AddDrugParam, Drug>(dispatchers) {
    override suspend fun execute(param: AddDrugParam): Drug = drugs.add(param)
}
