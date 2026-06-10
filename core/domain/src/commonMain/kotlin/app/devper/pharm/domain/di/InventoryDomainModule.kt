package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.inventory.AddDrugUseCase
import app.devper.pharm.domain.usecase.inventory.AddLotUseCase
import app.devper.pharm.domain.usecase.inventory.AddStockAdjustmentUseCase
import app.devper.pharm.domain.usecase.inventory.ClearStockCountDraftUseCase
import app.devper.pharm.domain.usecase.inventory.CreateStockCountUseCase
import app.devper.pharm.domain.usecase.inventory.DeleteLotUseCase
import app.devper.pharm.domain.usecase.inventory.GetDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.LoadStockCountDraftUseCase
import app.devper.pharm.domain.usecase.inventory.GetExpiringLotsUseCase
import app.devper.pharm.domain.usecase.inventory.GetLowStockDrugsUseCase
import app.devper.pharm.domain.usecase.inventory.GetReorderSuggestionsUseCase
import app.devper.pharm.domain.usecase.inventory.GetStockAdjustmentsUseCase
import app.devper.pharm.domain.usecase.inventory.GetStockCountsUseCase
import app.devper.pharm.domain.usecase.inventory.ListLotsUseCase
import app.devper.pharm.domain.usecase.inventory.PrintLabelsUseCase
import app.devper.pharm.domain.usecase.inventory.SaveStockCountDraftUseCase
import app.devper.pharm.domain.usecase.inventory.UpdateDrugUseCase
import app.devper.pharm.domain.usecase.inventory.WriteoffLotsUseCase
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
