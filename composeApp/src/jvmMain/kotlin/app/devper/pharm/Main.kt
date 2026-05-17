package app.devper.pharm

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.PdfDownloader
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.PdfDownloaderImpl
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
        single<PdfDownloader> { PdfDownloaderImpl() }
        single<ReceiptPrinter> { ReceiptPrinterImpl() }
    }

    startKoin { modules(jvmPlatformModule, appModule) }

    application {
        Window(onCloseRequest = ::exitApplication, title = "PharmacyApp") {
            App()
        }
    }
}
