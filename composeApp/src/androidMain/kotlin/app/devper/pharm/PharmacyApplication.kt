package app.devper.pharm

import android.app.Application
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.common.platform.MotionPreferences
import app.devper.pharm.common.platform.SecureStorage
import app.devper.pharm.common.platform.UnsavedChangesHandler
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.AndroidKeystoreSecureStorage
import app.devper.pharm.platform.ConnectivityObserverImpl
import app.devper.pharm.platform.FileDownloaderImpl
import app.devper.pharm.platform.FilePickerImpl
import app.devper.pharm.platform.MotionPreferencesImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import app.devper.pharm.platform.UnsavedChangesHandlerImpl
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

        val prefs = applicationContext.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        applyPersistedLocale(prefs.getString("ui.locale", null))

        val androidPlatformModule = module {
            single<Settings> {
                SharedPreferencesSettings(prefs)
            }
            single<SecureStorage> { AndroidKeystoreSecureStorage(applicationContext) }
            single { buildHttpClient(OkHttp, get<TokenStorage>(), enableLogging = BuildConfig.DEBUG) }

            single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.IO, default = Dispatchers.Default) }
            single<FileDownloader> { FileDownloaderImpl(applicationContext) }
            single<ConnectivityObserver> { ConnectivityObserverImpl(applicationContext) }
            single<FilePicker> { FilePickerImpl() }
            single<ReceiptPrinter> { ReceiptPrinterImpl() }
            single<MotionPreferences> { MotionPreferencesImpl(applicationContext) }
            single<UnsavedChangesHandler> { UnsavedChangesHandlerImpl() }
        }

        startKoin {
            androidContext(this@PharmacyApplication)
            modules(androidPlatformModule, appModule)
        }
    }
}

private fun applyPersistedLocale(wire: String?) {
    val tag = when (wire?.lowercase()) {
        "th" -> "th"
        "en" -> "en"
        else -> null
    } ?: return
    java.util.Locale.setDefault(java.util.Locale.forLanguageTag(tag))
}
