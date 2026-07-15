package app.devper.pharm

import androidx.compose.ui.window.ComposeUIViewController
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
import app.devper.pharm.platform.ConnectivityObserverImpl
import app.devper.pharm.platform.FileDownloaderImpl
import app.devper.pharm.platform.FilePickerImpl
import app.devper.pharm.platform.KeychainSecureStorage
import app.devper.pharm.platform.MotionPreferencesImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import app.devper.pharm.platform.UnsavedChangesHandlerImpl
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.darwin.Darwin
import kotlin.concurrent.Volatile
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

@Volatile
private var koinStarted = false

private fun ensureKoinStarted() {
    if (koinStarted) return
    val defaults = NSUserDefaults.standardUserDefaults
    applyPersistedLocale(defaults, defaults.stringForKey("ui.locale"))
    val iosPlatformModule = module {
        single<Settings> { NSUserDefaultsSettings(defaults) }
        single<SecureStorage> { KeychainSecureStorage() }
        single { buildHttpClient(Darwin, get<TokenStorage>()) }

        single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.Default, default = Dispatchers.Default) }
        single<FileDownloader> { FileDownloaderImpl(logger = get()) }
        single<ConnectivityObserver> { ConnectivityObserverImpl() }
        single<FilePicker> { FilePickerImpl() }
        single<ReceiptPrinter> { ReceiptPrinterImpl() }
        single<MotionPreferences> { MotionPreferencesImpl() }
        single<UnsavedChangesHandler> { UnsavedChangesHandlerImpl() }
    }
    startKoin { modules(iosPlatformModule, appModule) }
    koinStarted = true
}

private fun applyPersistedLocale(defaults: NSUserDefaults, wire: String?) {
    val tag = when (wire?.lowercase()) {
        "th" -> "th"
        "en" -> "en"
        else -> null
    } ?: return
    defaults.setObject(listOf(tag), forKey = "AppleLanguages")
    defaults.synchronize()
}

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController {
    ensureKoinStarted()
    App()
}
