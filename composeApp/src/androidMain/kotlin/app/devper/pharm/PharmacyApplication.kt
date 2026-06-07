package app.devper.pharm

import android.app.Application
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.common.platform.SecureStorage
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.AndroidKeystoreSecureStorage
import app.devper.pharm.platform.ConnectivityObserverImpl
import app.devper.pharm.platform.FileDownloaderImpl
import app.devper.pharm.platform.FilePickerImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings
import io.ktor.client.engine.okhttp.OkHttp
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.dsl.module

private const val PREFS_NAME = "pharmacy.prefs"

class PharmacyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val androidPlatformModule = module {
            single<Settings> {
                val prefs = applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                SharedPreferencesSettings(prefs)
            }
            single<SecureStorage> { AndroidKeystoreSecureStorage(applicationContext) }
            single { buildHttpClient(OkHttp, get<TokenStorage>()) }

            single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.IO, default = Dispatchers.Default) }
            single<FileDownloader> { FileDownloaderImpl(applicationContext) }
            single<ConnectivityObserver> { ConnectivityObserverImpl(applicationContext) }
            single<FilePicker> { FilePickerImpl() }
            single<ReceiptPrinter> { ReceiptPrinterImpl() }
        }

        startKoin {
            androidContext(this@PharmacyApplication)
            modules(androidPlatformModule, appModule)
        }
    }
}
