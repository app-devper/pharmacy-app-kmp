package app.devper.pharm.domain.usecase

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.ReorderSuggestion
import app.devper.pharm.domain.param.ReorderSuggestionsParam
import app.devper.pharm.domain.repository.DrugRepository

class GetReorderSuggestionsUseCase(private val drugs: DrugRepository, dispatchers: AppDispatchers) :
    BaseUseCase<ReorderSuggestionsParam, List<ReorderSuggestion>>(dispatchers) {
    override suspend fun execute(param: ReorderSuggestionsParam): List<ReorderSuggestion> =
        drugs.reorderSuggestions(param)
    suspend operator fun invoke(): Result<List<ReorderSuggestion>> = invoke(ReorderSuggestionsParam())
}
