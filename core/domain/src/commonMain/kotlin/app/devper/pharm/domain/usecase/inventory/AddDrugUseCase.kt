package app.devper.pharm.domain.usecase.inventory

import app.devper.pharm.domain.usecase.BaseUseCase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.param.inventory.AddDrugParam
import app.devper.pharm.domain.repository.inventory.DrugRepository

class AddDrugUseCase(private val drugs: DrugRepository, dispatchers: AppDispatchers) : BaseUseCase<AddDrugParam, Drug>(dispatchers) {
    override suspend fun execute(param: AddDrugParam): Drug = drugs.add(param)
}
