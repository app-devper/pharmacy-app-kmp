package app.devper.pharm.di

import app.devper.pharm.presentation.AppViewModel
import app.devper.pharm.presentation.auth.LoginViewModel
import app.devper.pharm.presentation.bulkimport.BulkImportViewModel
import app.devper.pharm.presentation.customers.CustomerDetailViewModel
import app.devper.pharm.presentation.customers.CustomerFormViewModel
import app.devper.pharm.presentation.customers.CustomersListViewModel
import app.devper.pharm.presentation.expiry.ExpiryViewModel
import app.devper.pharm.presentation.help.HelpViewModel
import app.devper.pharm.presentation.imports.ImportDetailViewModel
import app.devper.pharm.presentation.imports.ImportFormViewModel
import app.devper.pharm.presentation.imports.ImportsListViewModel
import app.devper.pharm.presentation.ky.Ky9ViewModel
import app.devper.pharm.presentation.ky.KyListViewModel
import app.devper.pharm.presentation.movements.MovementsViewModel
import app.devper.pharm.presentation.offlinesync.OfflineSyncViewModel
import app.devper.pharm.presentation.planning.LowStockViewModel
import app.devper.pharm.presentation.planning.ReorderSuggestionsViewModel
import app.devper.pharm.presentation.profile.ProfileViewModel
import app.devper.pharm.presentation.reports.EodViewModel
import app.devper.pharm.presentation.reports.ProfitViewModel
import app.devper.pharm.presentation.reports.ReportsViewModel
import app.devper.pharm.presentation.saleshistory.SalesHistoryViewModel
import app.devper.pharm.presentation.sell.SellViewModel
import app.devper.pharm.presentation.sell.flow.CheckoutViewModel
import app.devper.pharm.presentation.sell.flow.CustomerPickerViewModel
import app.devper.pharm.presentation.sell.flow.DrugPickerViewModel
import app.devper.pharm.presentation.sell.flow.ParkedCartViewModel
import app.devper.pharm.presentation.sell.flow.VoidSaleViewModel
import app.devper.pharm.presentation.settings.SettingsEditorViewModel
import app.devper.pharm.presentation.stock.DrugFormViewModel
import app.devper.pharm.presentation.stock.DrugLotsViewModel
import app.devper.pharm.presentation.stock.StockAdjustmentsViewModel
import app.devper.pharm.presentation.stock.StockViewModel
import app.devper.pharm.presentation.stockcount.StockCountFormViewModel
import app.devper.pharm.presentation.stockcount.StockCountsListViewModel
import app.devper.pharm.presentation.suppliers.SupplierFormViewModel
import app.devper.pharm.presentation.suppliers.SuppliersListViewModel
import app.devper.pharm.presentation.users.UserFormViewModel
import app.devper.pharm.presentation.users.UsersListViewModel
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.common.print.ReceiptTemplate
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import kotlin.test.AfterTest
import kotlin.test.Test

private class MemorySettings : Settings {
    private val store = mutableMapOf<String, Any?>()
    override val keys: Set<String> get() = store.keys
    override val size: Int get() = store.size
    override fun clear() = store.clear()
    override fun remove(key: String) { store.remove(key) }
    override fun hasKey(key: String): Boolean = store.containsKey(key)
    override fun putInt(key: String, value: Int) { store[key] = value }
    override fun getInt(key: String, defaultValue: Int): Int = (store[key] as? Int) ?: defaultValue
    override fun getIntOrNull(key: String): Int? = store[key] as? Int
    override fun putLong(key: String, value: Long) { store[key] = value }
    override fun getLong(key: String, defaultValue: Long): Long = (store[key] as? Long) ?: defaultValue
    override fun getLongOrNull(key: String): Long? = store[key] as? Long
    override fun putString(key: String, value: String) { store[key] = value }
    override fun getString(key: String, defaultValue: String): String = (store[key] as? String) ?: defaultValue
    override fun getStringOrNull(key: String): String? = store[key] as? String
    override fun putFloat(key: String, value: Float) { store[key] = value }
    override fun getFloat(key: String, defaultValue: Float): Float = (store[key] as? Float) ?: defaultValue
    override fun getFloatOrNull(key: String): Float? = store[key] as? Float
    override fun putDouble(key: String, value: Double) { store[key] = value }
    override fun getDouble(key: String, defaultValue: Double): Double = (store[key] as? Double) ?: defaultValue
    override fun getDoubleOrNull(key: String): Double? = store[key] as? Double
    override fun putBoolean(key: String, value: Boolean) { store[key] = value }
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = (store[key] as? Boolean) ?: defaultValue
    override fun getBooleanOrNull(key: String): Boolean? = store[key] as? Boolean
}

private val testPlatformModule = module {
    single<Settings> { MemorySettings() }
    single {
        HttpClient(MockEngine { respond(content = "{}", status = HttpStatusCode.OK) })
    }
    single { AppDispatchers(Dispatchers.Unconfined, Dispatchers.Unconfined, Dispatchers.Unconfined) }
    single<FileDownloader> {
        object : FileDownloader {
            override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> =
                Result.success("noop")
        }
    }
    single<ConnectivityObserver> {
        object : ConnectivityObserver {
            override val online = kotlinx.coroutines.flow.flowOf(true)
        }
    }
    single<FilePicker> {
        object : FilePicker {
            override suspend fun pickJsonFile(): Result<String?> = Result.success(null)
        }
    }
    single<ReceiptPrinter> {
        object : ReceiptPrinter {
            override fun print(template: ReceiptTemplate): Boolean = true
        }
    }
}

class AppModuleWiringTest {

    @AfterTest
    fun teardown() {
        stopKoin()
    }

    @Test
    fun every_view_model_resolves_from_app_module() {
        val koin = startKoin {
            modules(testPlatformModule, appModule)
        }.koin

        koin.get<AppViewModel>()

        koin.get<LoginViewModel>()

        koin.get<CustomersListViewModel>()
        koin.get<CustomerFormViewModel>()
        koin.get<CustomerDetailViewModel>()

        koin.get<SuppliersListViewModel>()
        koin.get<SupplierFormViewModel>()

        koin.get<StockViewModel>()
        koin.get<DrugFormViewModel>()
        koin.get<DrugLotsViewModel>()
        koin.get<StockAdjustmentsViewModel>()
        koin.get<StockCountsListViewModel>()
        koin.get<StockCountFormViewModel>()
        koin.get<ExpiryViewModel>()
        koin.get<LowStockViewModel>()
        koin.get<ReorderSuggestionsViewModel>()

        koin.get<Ky9ViewModel>()
        koin.get<KyListViewModel>()

        koin.get<OfflineSyncViewModel>()
        koin.get<HelpViewModel>()

        koin.get<ImportsListViewModel>()
        koin.get<ImportFormViewModel>()
        koin.get<ImportDetailViewModel>()
        koin.get<BulkImportViewModel>()

        koin.get<ReportsViewModel>()
        koin.get<ProfitViewModel>()
        koin.get<EodViewModel>()
        koin.get<MovementsViewModel>()

        koin.get<SellViewModel>()
        koin.get<CheckoutViewModel>()
        koin.get<DrugPickerViewModel>()
        koin.get<CustomerPickerViewModel>()
        koin.get<ParkedCartViewModel>()
        koin.get<VoidSaleViewModel>()
        koin.get<SalesHistoryViewModel>()

        koin.get<SettingsEditorViewModel>()

        koin.get<ProfileViewModel>()

        koin.get<UsersListViewModel>()
        koin.get<UserFormViewModel>()
    }
}
