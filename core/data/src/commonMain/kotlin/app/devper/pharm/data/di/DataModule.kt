package app.devper.pharm.data.di

import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.network.AppJson
import app.devper.pharm.data.remote.api.AuthApi
import app.devper.pharm.data.remote.api.CustomerApi
import app.devper.pharm.data.remote.api.DrugApi
import app.devper.pharm.data.remote.api.ExpiringLotsApi
import app.devper.pharm.data.remote.api.ExportApi
import app.devper.pharm.data.remote.api.KyApi
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
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.domain.repository.AuthRepository
import app.devper.pharm.domain.repository.CartRepository
import app.devper.pharm.domain.repository.CustomerRepository
import app.devper.pharm.domain.repository.DrugRepository
import app.devper.pharm.domain.repository.ExpiringLotsRepository
import app.devper.pharm.domain.repository.ExportRepository
import app.devper.pharm.domain.repository.KyRepository
import app.devper.pharm.domain.repository.LotsRepository
import app.devper.pharm.domain.repository.MovementsRepository
import app.devper.pharm.domain.repository.PurchaseOrderRepository
import app.devper.pharm.domain.repository.ReportsRepository
import app.devper.pharm.domain.repository.SaleHistoryRepository
import app.devper.pharm.domain.repository.SaleRepository
import app.devper.pharm.domain.repository.SettingsRepository
import app.devper.pharm.domain.repository.UiPreferencesRepository
import app.devper.pharm.data.repository.UiPreferencesRepositoryImpl
import app.devper.pharm.domain.repository.StockAdjustmentsRepository
import app.devper.pharm.domain.repository.StockCountsRepository
import app.devper.pharm.domain.repository.OfflineSaleQueue
import app.devper.pharm.domain.repository.ProfileRepository
import app.devper.pharm.domain.repository.SupplierRepository
import app.devper.pharm.domain.repository.UsersRepository
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val dataModule = module {
    single { ApiConfig() }
    single { AppJson }

    single { TokenStorage(get()) }
    single { ParkedCartStorage(get()) }
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
