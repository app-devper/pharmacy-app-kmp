---
name: kmp-platform
description: Cross-platform implementations in a Compose Multiplatform project via interface + Koin (NOT expect/actual) — interface in :core:common, impls in :composeApp/<plat>Main/platform/, bound per-platform in Main*.kt. Use when adding platform-specific behavior (file IO, printing, settings storage, HTTP engine, biometric, …).
---

# kmp-platform

The project bans `expect`/`actual` everywhere (audit rule A27). Cross-platform behavior is
expressed as **a regular Kotlin interface** in `:core:common`, with platform-specific impls in
`:composeApp/<plat>Main/platform/`, bound per platform via Koin in each `Main*.kt`.

## Why no expect/actual

- **Compilation**: `expect`/`actual` requires the same module to declare both halves, which
  forces every cross-platform contract to live in a module with all 5 platform source sets. Our
  cleaner constraint is "only `:composeApp` has platform folders", which keeps `:core:*` and
  `:features:*` pure commonMain and dramatically faster to compile.
- **Testability**: an interface is trivially fakeable; an `actual class` is not.
- **Discoverability**: `Find Usages` on an interface name finds the impls. On an `expect class`
  it doesn't reliably show actuals across modules.

## 1. Declare the contract in `:core:common`

```kotlin
// core/common/src/commonMain/kotlin/<base>/common/platform/FileDownloader.kt
interface FileDownloader {
    suspend fun saveBinaryFile(filename: String, mimeType: String, bytes: ByteArray): String
}

object MimeType {
    const val CSV = "text/csv"
    const val PDF = "application/pdf"
    const val JSON = "application/json"
}
```

Interface lives in `:core:common`; **no project deps** other than kotlinx. Throw typed
`AppException` (`StorageException`, `UnsupportedPlatformException`) on failures.

## 2. Implement per platform in `:composeApp/<plat>Main/platform/`

Only `:composeApp` may have `androidMain` / `iosMain` / `jvmMain` / `wasmJsMain` source folders.
Each platform impl is a single class:

### androidMain
```kotlin
// composeApp/src/androidMain/kotlin/<base>/platform/FileDownloaderImpl.kt
class FileDownloaderImpl(private val context: Context) : FileDownloader {
    override suspend fun saveBinaryFile(filename: String, mimeType: String, bytes: ByteArray): String {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, filename)
            put(MediaStore.Downloads.MIME_TYPE, mimeType)
        }
        val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw StorageException("ไม่สามารถสร้างไฟล์ปลายทาง")
        context.contentResolver.openOutputStream(uri)?.use { it.write(bytes) }
            ?: throw StorageException("เปิดไฟล์เพื่อเขียนไม่สำเร็จ")
        return filename
    }
}
```

### iosMain
```kotlin
// composeApp/src/iosMain/kotlin/<base>/platform/FileDownloaderImpl.kt
class FileDownloaderImpl : FileDownloader {
    @OptIn(ExperimentalForeignApi::class)
    override suspend fun saveBinaryFile(filename: String, mimeType: String, bytes: ByteArray): String {
        val dir = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, true)
            .firstOrNull() as? String ?: throw StorageException("Document directory not found")
        val path = "$dir/$filename"
        val nsData = bytes.usePinned { NSData.dataWithBytes(it.addressOf(0), bytes.size.toULong()) }
        if (!nsData.writeToFile(path, true)) throw StorageException("เขียนไฟล์ไม่สำเร็จ: $path")
        return path
    }
}
```

### jvmMain
```kotlin
// composeApp/src/jvmMain/kotlin/<base>/platform/FileDownloaderImpl.kt
class FileDownloaderImpl : FileDownloader {
    override suspend fun saveBinaryFile(filename: String, mimeType: String, bytes: ByteArray): String {
        val downloads = Paths.get(System.getProperty("user.home"), "Downloads")
        Files.createDirectories(downloads)
        val target = downloads.resolve(filename)
        Files.write(target, bytes)
        return target.toAbsolutePath().toString()
    }
}
```

### wasmJsMain
```kotlin
// composeApp/src/wasmJsMain/kotlin/<base>/platform/FileDownloaderImpl.kt
class FileDownloaderImpl : FileDownloader {
    override suspend fun saveBinaryFile(filename: String, mimeType: String, bytes: ByteArray): String {
        val blob = Blob(bytes.toJsArray(), BlobPropertyBag(type = mimeType))
        val url = URL.createObjectURL(blob)
        val anchor = document.createElement("a") as HTMLAnchorElement
        anchor.href = url
        anchor.download = filename
        anchor.click()
        URL.revokeObjectURL(url)
        return filename
    }
}
```

(`UnsupportedPlatformException` is a valid impl if a platform genuinely can't do the operation.)

## 3. Bind per platform in Koin (each platform's `Main*.kt`)

Each platform's entry point declares a Koin module that binds its `FileDownloaderImpl` to
`FileDownloader`:

```kotlin
// composeApp/src/androidMain/kotlin/<base>/MainActivity.kt
private val androidPlatformModule = module {
    single<FileDownloader> { FileDownloaderImpl(androidContext()) }
    // + other Android-specific bindings: Settings, HttpClient engine, AppDispatchers, …
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        startKoin {
            androidContext(this@MainActivity)
            modules(appModule + androidPlatformModule)        // appModule from :composeApp/di
        }
        setContent { App() }
    }
}
```

Same shape per platform — `MainViewController.kt` (iOS), `Main.kt` (JVM desktop), `Main.kt`
(wasm). Each `<plat>PlatformModule` binds:
- `FileDownloader` → `FileDownloaderImpl`
- `Settings` (multiplatform-settings) → its platform factory
- `HttpClient` engine (`OkHttp` / `Darwin` / `OkHttp` / `Js`) → wrapped Ktor `HttpClient`
- `AppDispatchers` → `AppDispatchers(io = Dispatchers.IO …)` with the platform-appropriate IO
  dispatcher

The shared `appModule` (in `:composeApp/di/AppModule.kt`) `includes(commonModule, domainModule,
dataModule, + every feature module)` and is the **same on every platform**. Platform differences
live in `<plat>PlatformModule`.

## 4. Use the interface from common code

In a use case / repository / VM:
```kotlin
class ExportCsvUseCase(
    private val downloader: FileDownloader,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportCsvParam, String>(dispatchers) {
    override suspend fun execute(param: ExportCsvParam): String =
        downloader.saveBinaryFile("export_${param.label}.csv", MimeType.CSV, param.bytes)
}
```

Common code never references `Context`, `NSDocumentDirectory`, `Paths.get`, `Blob` etc. — only
the interface.

## 5. Testing

In `commonTest`, write a `FakeFileDownloader` that records the call:
```kotlin
class FakeFileDownloader(private val throws: Boolean = false) : FileDownloader {
    var lastFilename: String? = null
    var lastMime: String? = null
    var lastBytes: ByteArray? = null
    override suspend fun saveBinaryFile(filename: String, mimeType: String, bytes: ByteArray): String {
        if (throws) throw StorageException("fake")
        lastFilename = filename; lastMime = mimeType; lastBytes = bytes
        return filename
    }
}
```
…then test use cases / VMs that depend on `FileDownloader` with the fake. No actual platform IO
needed in unit tests.

## 6. Common interfaces in this pattern

| Concern | Common interface | Platform impl uses |
|---|---|---|
| File save / download | `FileDownloader` | MediaStore / NSDocumentDirectory / java.nio / `<a download>` |
| Receipt printing | `ReceiptPrinter` | Android print framework / `UIPrintInteractionController` / `PrinterJob` / `window.print()` |
| Secure storage | wrap `multiplatform-settings` | EncryptedSharedPreferences / Keychain / java.util.prefs / localStorage |
| HTTP engine | `HttpClient` engine wired into Ktor | OkHttp / Darwin / OkHttp / Js |
| Dispatchers | `AppDispatchers` data class | `Dispatchers.IO` / `Dispatchers.Default` (per platform) |
| Logger | `Logger` interface | Logcat / NSLog / println / console.log |
| Clock / time | `Clock` interface | wallclock / fixed-clock-in-tests |

The pattern is the same every time: interface in `:core:common`, impls in
`:composeApp/<plat>Main/platform/`, bound per platform in `Main*.kt`'s Koin module.

## 7. Anti-patterns to flag

- A new `expect class` / `expect fun` / `expect val` anywhere → **A27**, fail audit.
- A `:core:*` or `:features:<x>` module growing an `androidMain`/`iosMain`/`jvmMain`/`wasmJsMain`
  folder → **A26**, fail audit.
- Common code referencing a platform type (e.g. `java.io.File`, `android.content.Context`,
  `platform.Foundation.*`) → wrap in an interface.
- Using `Dispatchers.IO` directly from common code → take `AppDispatchers` and use
  `dispatchers.io`.
- A test depending on a real platform impl → write a fake against the interface.
