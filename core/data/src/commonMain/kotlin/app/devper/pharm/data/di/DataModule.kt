package app.devper.pharm.data.di

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.network.AppJson
import app.devper.pharm.data.remote.api.AuthApi
import app.devper.pharm.data.remote.api.CustomerApi
import app.devper.pharm.data.remote.api.DrugApi
import app.devper.pharm.data.remote.api.ExpiringLotsApi
import app.devper.pharm.data.remote.api.ExportApi
import app.devper.pharm.data.remote.api.KyApi
import app.devper.pharm.data.remote.api.LabelApi
import app.devper.pharm.data.remote.api.LotsApi
import app.devper.pharm.data.remote.api.MovementsApi
import app.devper.pharm.data.remote.api.ProfileApi
import app.devper.pharm.data.remote.api.PurchaseOrderApi
import app.devper.pharm.data.remote.api.ReportsApi
import app.devper.pharm.data.remote.api.SaleApi
import app.devper.pharm.data.remote.api.SaleHistoryApi
import app.devper.pharm.data.remote.api.SettingsApi
import app.devper.pharm.data.remote.api.StockAdjustmentApi
import app.devper.pharm.data.remote.api.StockCountsApi
import app.devper.pharm.data.remote.api.SupplierApi
import app.devper.pharm.data.remote.api.UsersApi
import app.devper.pharm.data.repository.AuthRepositoryImpl
import app.devper.pharm.data.repository.CartRepositoryImpl
import app.devper.pharm.data.repository.CustomerRepositoryImpl
import app.devper.pharm.data.repository.DrugRepositoryImpl
import app.devper.pharm.data.repository.ExpiringLotsRepositoryImpl
import app.devper.pharm.data.repository.ExportRepositoryImpl
import app.devper.pharm.data.repository.KyRepositoryImpl
import app.devper.pharm.data.repository.LabelRepositoryImpl
import app.devper.pharm.data.repository.LotsRepositoryImpl
import app.devper.pharm.data.repository.MovementsRepositoryImpl
import app.devper.pharm.data.repository.ProfileRepositoryImpl
import app.devper.pharm.data.repository.PurchaseOrderRepositoryImpl
import app.devper.pharm.data.repository.ReportsRepositoryImpl
import app.devper.pharm.data.repository.SaleHistoryRepositoryImpl
import app.devper.pharm.data.repository.SaleRepositoryImpl
import app.devper.pharm.data.repository.SettingsRepositoryImpl
import app.devper.pharm.data.repository.StockAdjustmentsRepositoryImpl
import app.devper.pharm.data.repository.StockCountsRepositoryImpl
import app.devper.pharm.data.repository.SupplierRepositoryImpl
import app.devper.pharm.data.repository.UsersRepositoryImpl
import app.devper.pharm.data.storage.OfflineSaleQueueImpl
import app.devper.pharm.data.storage.ParkedCartStorage
import app.devper.pharm.data.storage.StockCountDraftStorage
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.domain.repository.auth.AuthRepository
import app.devper.pharm.domain.repository.sales.CartRepository
import app.devper.pharm.domain.repository.customers.CustomerRepository
import app.devper.pharm.domain.repository.inventory.DrugRepository
import app.devper.pharm.domain.repository.inventory.ExpiringLotsRepository
import app.devper.pharm.domain.repository.ky.ExportRepository
import app.devper.pharm.domain.repository.ky.KyRepository
import app.devper.pharm.domain.repository.inventory.LabelRepository
import app.devper.pharm.domain.repository.inventory.LotsRepository
import app.devper.pharm.domain.repository.reports.MovementsRepository
import app.devper.pharm.domain.repository.purchasing.PurchaseOrderRepository
import app.devper.pharm.domain.repository.reports.ReportsRepository
import app.devper.pharm.domain.repository.sales.SaleHistoryRepository
import app.devper.pharm.domain.repository.sales.SaleRepository
import app.devper.pharm.domain.repository.settings.SettingsRepository
import app.devper.pharm.domain.repository.settings.UiPreferencesRepository
import app.devper.pharm.data.repository.UiPreferencesRepositoryImpl
import app.devper.pharm.domain.repository.inventory.StockAdjustmentsRepository
import app.devper.pharm.domain.repository.inventory.StockCountDraftRepository
import app.devper.pharm.domain.repository.inventory.StockCountsRepository
import app.devper.pharm.domain.repository.offlinesync.OfflineSaleQueue
import app.devper.pharm.domain.repository.profile.ProfileRepository
import app.devper.pharm.domain.repository.suppliers.SupplierRepository
import app.devper.pharm.domain.repository.users.UsersRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

fun dataModule(apiConfig: ApiConfig = ApiConfig()) = module {
    single { apiConfig }
    single { AppJson }

    single { TokenStorage(get()) }
    single { ParkedCartStorage(get()) }
    singleOf(::StockCountDraftStorage) bind StockCountDraftRepository::class
    singleOf(::OfflineSaleQueueImpl) bind OfflineSaleQueue::class

    singleOf(::AuthApi)
    singleOf(::AuthRepositoryImpl) bind AuthRepository::class

    singleOf(::CustomerApi)
    singleOf(::CustomerRepositoryImpl) bind CustomerRepository::class

    singleOf(::SupplierApi)
    singleOf(::SupplierRepositoryImpl) bind SupplierRepository::class

    singleOf(::DrugApi)
    singleOf(::DrugRepositoryImpl) bind DrugRepository::class

    singleOf(::LotsApi)
    singleOf(::LotsRepositoryImpl) bind LotsRepository::class

    singleOf(::StockAdjustmentApi)
    singleOf(::StockAdjustmentsRepositoryImpl) bind StockAdjustmentsRepository::class

    singleOf(::StockCountsApi)
    singleOf(::StockCountsRepositoryImpl) bind StockCountsRepository::class

    singleOf(::ExpiringLotsApi)
    singleOf(::ExpiringLotsRepositoryImpl) bind ExpiringLotsRepository::class

    singleOf(::ProfileApi)
    singleOf(::ProfileRepositoryImpl) bind ProfileRepository::class

    singleOf(::UsersApi)
    singleOf(::UsersRepositoryImpl) bind UsersRepository::class

    singleOf(::PurchaseOrderApi)
    singleOf(::PurchaseOrderRepositoryImpl) bind PurchaseOrderRepository::class

    singleOf(::KyApi)
    singleOf(::KyRepositoryImpl) bind KyRepository::class

    singleOf(::ExportApi)
    singleOf(::ExportRepositoryImpl) bind ExportRepository::class

    singleOf(::LabelApi)
    singleOf(::LabelRepositoryImpl) bind LabelRepository::class

    singleOf(::ReportsApi)
    singleOf(::ReportsRepositoryImpl) bind ReportsRepository::class

    singleOf(::MovementsApi)
    singleOf(::MovementsRepositoryImpl) bind MovementsRepository::class

    singleOf(::SaleApi)
    singleOf(::SaleRepositoryImpl) bind SaleRepository::class

    singleOf(::SaleHistoryApi)
    singleOf(::SaleHistoryRepositoryImpl) bind SaleHistoryRepository::class

    singleOf(::CartRepositoryImpl) bind CartRepository::class

    singleOf(::SettingsApi)
    singleOf(::SettingsRepositoryImpl) bind SettingsRepository::class

    singleOf(::UiPreferencesRepositoryImpl) bind UiPreferencesRepository::class
}
