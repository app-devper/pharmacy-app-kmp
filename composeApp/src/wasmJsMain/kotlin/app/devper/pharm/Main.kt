package app.devper.pharm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.common.platform.MotionPreferences
import app.devper.pharm.common.platform.PointerPreferences
import app.devper.pharm.common.platform.SecureStorage
import app.devper.pharm.common.platform.UnsavedChangesHandler
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.ApiConfig
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.network.localQaApiBaseUrl
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.ConnectivityObserverImpl
import app.devper.pharm.platform.FileDownloaderImpl
import app.devper.pharm.platform.FilePickerImpl
import app.devper.pharm.platform.MotionPreferencesImpl
import app.devper.pharm.platform.PointerPreferencesImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import app.devper.pharm.platform.WebSecureStorage
import app.devper.pharm.platform.UnsavedChangesHandlerImpl
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import io.ktor.client.engine.js.Js
import kotlinx.browser.window
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
        single { buildHttpClient(Js, get<TokenStorage>(), get()) }

        single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.Default, default = Dispatchers.Default) }
        single<FileDownloader> { FileDownloaderImpl() }
        single<ConnectivityObserver> { ConnectivityObserverImpl() }
        single<FilePicker> { FilePickerImpl() }
        single<ReceiptPrinter> { ReceiptPrinterImpl() }
        single<MotionPreferences> { MotionPreferencesImpl() }
        single<PointerPreferences> { PointerPreferencesImpl() }
        single<UnsavedChangesHandler> { UnsavedChangesHandlerImpl() }
    }
    startKoin { modules(webPlatformModule, appModule(resolveApiConfig())) }

    ComposeViewport(content = {
        LaunchedEffect(Unit) {
            installCanvasFocusStyle()
            hideBootLoader()
        }
        App()
    })
}

private fun resolveApiConfig(): ApiConfig {
    val apiBaseUrl = localQaApiBaseUrl(
        pageHost = window.location.hostname,
        rawQuery = window.location.search,
    )
    if (apiBaseUrl != null) println("Using local QA API: $apiBaseUrl")
    return apiBaseUrl?.let(::ApiConfig) ?: ApiConfig()
}

private fun installCanvasFocusStyle(): Unit = js(
    """
    {
        var hosts = document.querySelectorAll('div');
        for (var i = 0; i < hosts.length; i++) {
            var root = hosts[i].shadowRoot;
            if (root && root.querySelector('canvas')) {
                var style = document.createElement('style');
                style.textContent = 'canvas:focus-visible { outline: 2px solid #1B83D8 !important; outline-offset: -2px !important; } @media (prefers-color-scheme: dark) { canvas:focus-visible { outline-color: #64a9e8 !important; } }';
                root.appendChild(style);
                break;
            }
        }
    }
    """,
)

private fun hideBootLoader(): Unit = js(
    """
    {
        var el = document.getElementById('app-loading');
        if (el) {
            el.classList.add('done');
            setTimeout(function () { el.remove(); }, 250);
        }
    }
    """,
)

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
