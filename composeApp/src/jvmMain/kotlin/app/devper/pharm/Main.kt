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
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.ConnectivityObserverImpl
import app.devper.pharm.platform.FileDownloaderImpl
import app.devper.pharm.platform.FilePickerImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.java.Java
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module
import java.util.prefs.Preferences

fun main() {
    val jvmPlatformModule = module {
        single<Settings> { PreferencesSettings(Preferences.userRoot().node("pharmacy.app")) }
        single { buildHttpClient(Java, get<TokenStorage>()) }

        single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.IO, default = Dispatchers.Default) }
        single<FileDownloader> { FileDownloaderImpl(logger = get()) }
        single<ConnectivityObserver> { ConnectivityObserverImpl() }
        single<FilePicker> { FilePickerImpl() }
        single<ReceiptPrinter> { ReceiptPrinterImpl() }
    }

    startKoin { modules(jvmPlatformModule, appModule) }

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
