package app.devper.pharm.domain.di

import app.devper.pharm.domain.usecase.purchasing.AddPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.BulkImportDrugsUseCase
import app.devper.pharm.domain.usecase.purchasing.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.GetPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.purchasing.GetPurchaseOrdersUseCase
import app.devper.pharm.domain.usecase.purchasing.UpdatePurchaseOrderUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val purchasingDomainModule = module {
    factoryOf(::GetPurchaseOrdersUseCase)
    factoryOf(::GetPurchaseOrderUseCase)
    factoryOf(::AddPurchaseOrderUseCase)
    factoryOf(::UpdatePurchaseOrderUseCase)
    factoryOf(::ConfirmPurchaseOrderUseCase)
    factoryOf(::DeletePurchaseOrderUseCase)
    factoryOf(::BulkImportDrugsUseCase)
}
