package app.devper.pharm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.common.platform.MotionPreferences
import app.devper.pharm.common.platform.SecureStorage
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.ConnectivityObserverImpl
import app.devper.pharm.platform.FileDownloaderImpl
import app.devper.pharm.platform.FilePickerImpl
import app.devper.pharm.platform.MotionPreferencesImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import app.devper.pharm.platform.WebSecureStorage
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import io.ktor.client.engine.js.Js
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    loadJsJodaTimezoneDatabase()
    val settings = StorageSettings()
    applyPersistedLocale(settings.getStringOrNull("ui.locale"))

    val webPlatformModule = module {
        single<Settings> { settings }
        single<SecureStorage> { WebSecureStorage() }
        single { buildHttpClient(Js, get<TokenStorage>()) }

        single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.Default, default = Dispatchers.Default) }
        single<FileDownloader> { FileDownloaderImpl() }
        single<ConnectivityObserver> { ConnectivityObserverImpl() }
        single<FilePicker> { FilePickerImpl() }
        single<ReceiptPrinter> { ReceiptPrinterImpl() }
        single<MotionPreferences> { MotionPreferencesImpl() }
    }
    startKoin { modules(webPlatformModule, appModule) }

    ComposeViewport(content = {
        App()
    })
}

private fun applyPersistedLocale(wire: String?) {
    val tag = when (wire?.lowercase()) {
        "th" -> "th"
        "en" -> "en"
        else -> null
    } ?: return
    overrideNavigatorLanguage(tag)
}

private fun overrideNavigatorLanguage(tag: String): Unit = js(
    """
    {
        try {
            Object.defineProperty(window.navigator, 'language', { value: tag, configurable: true });
            Object.defineProperty(window.navigator, 'languages', { value: [tag], configurable: true });
            document.documentElement.lang = tag;
        } catch (e) {
            console.warn('Locale override failed; browser will use system language.', e);
        }
    }
    """,
)
