package app.devper.pharm

import android.app.Application
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
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

private const val SECURE_PREFS_NAME = "pharmacy.secure.prefs"

class PharmacyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        val androidPlatformModule = module {
            single<Settings> { SharedPreferencesSettings(buildSecurePrefs()) }
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

    private fun buildSecurePrefs(): SharedPreferences {
        val masterKey = MasterKey.Builder(applicationContext)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
        return EncryptedSharedPreferences.create(
            applicationContext,
            SECURE_PREFS_NAME,
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }
}
