package app.devper.pharm

import androidx.compose.ui.window.ComposeUIViewController
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.FileDownloader
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.FileDownloaderImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import io.ktor.client.engine.darwin.Darwin
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

private var koinStarted = false

private fun ensureKoinStarted() {
    if (koinStarted) return
    val iosPlatformModule = module {
        single<Settings> { NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults) }
        single { buildHttpClient(Darwin, get<TokenStorage>()) }

        single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.Default, default = Dispatchers.Default) }
        single<FileDownloader> { FileDownloaderImpl() }
        single<ReceiptPrinter> { ReceiptPrinterImpl() }
    }
    startKoin { modules(iosPlatformModule, appModule) }
    koinStarted = true
}

@Suppress("FunctionName", "unused")
fun MainViewController() = ComposeUIViewController {
    ensureKoinStarted()
    App()
}
