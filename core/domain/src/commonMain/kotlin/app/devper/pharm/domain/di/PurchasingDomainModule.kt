package app.devper.pharm.domain.di

import app.devper.pharm.domain.parser.BulkImportJsonParser
import app.devper.pharm.domain.usecase.AddPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.BulkImportDrugsUseCase
import app.devper.pharm.domain.usecase.ConfirmPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.DeletePurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetPurchaseOrderUseCase
import app.devper.pharm.domain.usecase.GetPurchaseOrdersUseCase
import app.devper.pharm.domain.usecase.UpdatePurchaseOrderUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val purchasingDomainModule = module {
    singleOf(::BulkImportJsonParser)
    factoryOf(::GetPurchaseOrdersUseCase)
    factoryOf(::GetPurchaseOrderUseCase)
    factoryOf(::AddPurchaseOrderUseCase)
    factoryOf(::UpdatePurchaseOrderUseCase)
    factoryOf(::ConfirmPurchaseOrderUseCase)
    factoryOf(::DeletePurchaseOrderUseCase)
    factoryOf(::BulkImportDrugsUseCase)
}
