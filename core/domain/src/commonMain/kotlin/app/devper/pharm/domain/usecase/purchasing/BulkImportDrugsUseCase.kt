package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.BulkImportResult
import app.devper.pharm.domain.param.AddDrugParam
import app.devper.pharm.domain.repository.DrugRepository

class BulkImportDrugsUseCase(private val drugs: DrugRepository, dispatchers: AppDispatchers) :
    BaseUseCase<List<AddDrugParam>, BulkImportResult>(dispatchers) {
    override suspend fun execute(param: List<AddDrugParam>): BulkImportResult = drugs.bulkImport(param)
}
