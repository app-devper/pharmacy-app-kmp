package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.param.UpdateDrugParam
import app.devper.pharm.domain.repository.DrugRepository

class UpdateDrugUseCase(private val drugs: DrugRepository, dispatchers: AppDispatchers) : BaseUseCase<UpdateDrugParam, Unit>(dispatchers) {
    override suspend fun execute(param: UpdateDrugParam) = drugs.update(param)
}
