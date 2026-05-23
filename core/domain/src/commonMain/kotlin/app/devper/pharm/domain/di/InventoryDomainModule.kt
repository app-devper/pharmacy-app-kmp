package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.AddDrugUseCase
import app.devper.pharm.domain.usecase.AddLotUseCase
import app.devper.pharm.domain.usecase.AddStockAdjustmentUseCase
import app.devper.pharm.domain.usecase.ClearStockCountDraftUseCase
import app.devper.pharm.domain.usecase.CreateStockCountUseCase
import app.devper.pharm.domain.usecase.DeleteLotUseCase
import app.devper.pharm.domain.usecase.GetDrugsUseCase
import app.devper.pharm.domain.usecase.LoadStockCountDraftUseCase
import app.devper.pharm.domain.usecase.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.GetLowStockDrugsUseCase
import app.devper.pharm.domain.usecase.GetReorderSuggestionsUseCase
import app.devper.pharm.domain.usecase.GetStockAdjustmentsUseCase
import app.devper.pharm.domain.usecase.GetStockCountsUseCase
import app.devper.pharm.domain.usecase.ListLotsUseCase
import app.devper.pharm.domain.usecase.PrintLabelsUseCase
import app.devper.pharm.domain.usecase.SaveStockCountDraftUseCase
import app.devper.pharm.domain.usecase.UpdateDrugUseCase
import app.devper.pharm.domain.usecase.WriteoffLotsUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val inventoryDomainModule = module {
    factoryOf(::GetDrugsUseCase)
    factoryOf(::AddDrugUseCase)
    factoryOf(::UpdateDrugUseCase)
    factoryOf(::ListLotsUseCase)
    factoryOf(::AddLotUseCase)
    factoryOf(::DeleteLotUseCase)
    factoryOf(::GetStockAdjustmentsUseCase)
    factoryOf(::AddStockAdjustmentUseCase)
    factoryOf(::GetStockCountsUseCase)
    factoryOf(::CreateStockCountUseCase)
    factoryOf(::LoadStockCountDraftUseCase)
    factoryOf(::SaveStockCountDraftUseCase)
    factoryOf(::ClearStockCountDraftUseCase)
    factoryOf(::GetExpiringLotsUseCase)
    factoryOf(::WriteoffLotsUseCase)
    factoryOf(::GetLowStockDrugsUseCase)
    factoryOf(::GetReorderSuggestionsUseCase)
    factoryOf(::PrintLabelsUseCase)
}
