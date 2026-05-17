package app.devper.pharm

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.common.platform.PdfDownloader
import app.devper.pharm.common.print.ReceiptPrinter
import app.devper.pharm.data.network.buildHttpClient
import app.devper.pharm.data.storage.TokenStorage
import app.devper.pharm.di.appModule
import app.devper.pharm.platform.PdfDownloaderImpl
import app.devper.pharm.platform.ReceiptPrinterImpl
import com.russhwolf.settings.Settings
import com.russhwolf.settings.StorageSettings
import io.ktor.client.engine.js.Js
import kotlinx.coroutines.Dispatchers
import org.koin.core.context.startKoin
import org.koin.dsl.module

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val webPlatformModule = module {
        single<Settings> { StorageSettings() }
        single { buildHttpClient(Js, get<TokenStorage>()) }

        single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.Default, default = Dispatchers.Default) }
        single<PdfDownloader> { PdfDownloaderImpl() }
        single<ReceiptPrinter> { ReceiptPrinterImpl() }
    }
    startKoin { modules(webPlatformModule, appModule) }

    ComposeViewport(content = {
        App()
    })
}
