package app.devper.pharm

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import java.awt.Dimension
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.ConnectivityObserver
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.platform.FilePicker
import app.devper.pharm.common.platform.MotionPreferences
import app.devper.pharm.common.platform.PointerPreferences
import app.devper.pharm.common.platform.SecureStorage
import app.devper.pharm.common.platform.UnsavedChangesHandler
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.ConnectivityObserverImpl
import app.devper.pharm.platform.FileDownloaderImpl
import app.devper.pharm.platform.FilePickerImpl
import app.devper.pharm.platform.JvmSecureStorage
import app.devper.pharm.platform.MotionPreferencesImpl
import app.devper.pharm.platform.PointerPreferencesImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import app.devper.pharm.platform.UnsavedChangesHandlerImpl
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.java.Java
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.prefs.Preferences

fun main() {
    val rootPreferences = Preferences.userRoot().node("pharmacy.app")
    applyPersistedLocale(rootPreferences.get("ui.locale", null))

    val jvmPlatformModule = module {
        single<Settings> { PreferencesSettings(rootPreferences) }
        single<SecureStorage> { JvmSecureStorage() }
        single { buildHttpClient(Java, get<TokenStorage>(), get(), enableLogging = true) }

        single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.IO, default = Dispatchers.Default) }
        single<FileDownloader> { FileDownloaderImpl(logger = get()) }
        single<ConnectivityObserver> { ConnectivityObserverImpl() }
        single<FilePicker> { FilePickerImpl() }
        single<ReceiptPrinter> { ReceiptPrinterImpl() }
        single<MotionPreferences> { MotionPreferencesImpl() }
        single<PointerPreferences> { PointerPreferencesImpl() }
        single<UnsavedChangesHandler> { UnsavedChangesHandlerImpl() }
    }

    startKoin { modules(jvmPlatformModule, appModule()) }

    application {
        val windowState = rememberWindowState(width = 1100.dp, height = 760.dp)
        Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "PharmacyApp",
        ) {
            LaunchedEffect(Unit) {
                window.minimumSize = Dimension(600, 600)
            }
            App()
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
