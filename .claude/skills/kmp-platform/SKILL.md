---
name: kmp-platform
description: Cross-platform behaviour in the pharmacy app via interface + Koin, never expect/actual — interface in :core:common, impls in :composeApp/<plat>Main/platform/, bound per platform in each entry point. Use when adding platform-specific behaviour (file IO, printing, secure storage, connectivity, HTTP engine).
---

# kmp-platform

`expect`/`actual` is banned project-wide (audit A27), and only `:composeApp`
may have platform source folders (A26). Cross-platform behaviour is a plain
Kotlin **interface** in `:core:common`, with one impl per platform in
`:composeApp/<plat>Main/kotlin/app/devper/pharm/platform/`, bound by Koin in
each platform's entry point.

Why: `expect`/`actual` would force every contract into a module carrying all
five source sets, so `:core:*` and `:features:*` stay pure `commonMain` and
compile far faster. Interfaces are also trivially fakeable, and `Find Usages`
on an interface name actually finds every impl.

## 1. The contracts

`core/common/src/commonMain/kotlin/app/devper/pharm/common/platform/`:

| Interface | Contract |
|---|---|
| `FileDownloader` | `suspend fun save(filename, mimeType, bytes): Result<String>` + a `MimeType` object (`Pdf`/`Csv`/`Xlsx`/`Json`/`PlainText`) |
| `FilePicker` | pick a file to read back |
| `SecureStorage` | token / credential storage |
| `ConnectivityObserver` | online/offline stream driving the offline queue |
| `MotionPreferences` | OS "reduce motion" → `LocalReducedMotion` |
| `PointerPreferences` | `val isTouchPrimary: Boolean` → 44dp vs 36dp touch targets |
| `UnsavedChangesHandler` | leave-confirmation for dirty forms |

Plus `common/print/ReceiptPrinter.kt`, and `AppDispatchers` (a `data class` of
`main` / `io` / `default`) in `common/AppDispatchers.kt`.

`:core:common` has **zero project dependencies** — these interfaces reference
only Kotlin and kotlinx types. Failures are typed `AppException`s
(`StorageException`, `UnsupportedPlatformException`), or a `Result` where the
caller is expected to branch.

## 2. One impl per platform

```
composeApp/src/androidMain/kotlin/app/devper/pharm/platform/
composeApp/src/iosMain/…      jvmMain/…      wasmJsMain/…
```

All four currently carry: `FileDownloaderImpl`, `FilePickerImpl`,
`ConnectivityObserverImpl`, `MotionPreferencesImpl`, `PointerPreferencesImpl`,
`UnsavedChangesHandlerImpl`, `ReceiptPrinterImpl`, plus a
platform-named secure storage (`AndroidKeystoreSecureStorage`,
`KeychainSecureStorage`, `JvmSecureStorage`, `WebSecureStorage`).

The mechanics per platform:

| Concern | Android | iOS | JVM | wasmJs |
|---|---|---|---|---|
| File save | MediaStore | `NSDocumentDirectory` | `java.nio` → `~/Downloads` | `Blob` + `<a download>` |
| Secure storage | Keystore | Keychain | `java.util.prefs` | localStorage |
| Printing | print framework | `UIPrintInteractionController` | `PrinterJob` | `window.print()` |
| HTTP engine | OkHttp | Darwin | OkHttp | Js |

`UnsupportedPlatformException` is a legitimate implementation when a platform
genuinely cannot do the operation.

## 3. Bind in the platform entry point

Android — `composeApp/src/androidMain/…/PharmacyApplication.kt`:

```kotlin
val androidPlatformModule = module {
    single<Settings> { SharedPreferencesSettings(prefs) }
    single<SecureStorage> { AndroidKeystoreSecureStorage(applicationContext) }
    single { buildHttpClient(OkHttp, get<TokenStorage>(), get(), enableLogging = BuildConfig.DEBUG) }
    single { AppDispatchers(main = Dispatchers.Main, io = Dispatchers.IO, default = Dispatchers.Default) }
    single<FileDownloader> { FileDownloaderImpl(applicationContext) }
    single<ConnectivityObserver> { ConnectivityObserverImpl(applicationContext) }
    single<FilePicker> { FilePickerImpl() }
    single<ReceiptPrinter> { ReceiptPrinterImpl() }
    single<MotionPreferences> { MotionPreferencesImpl(applicationContext) }
    single<PointerPreferences> { PointerPreferencesImpl() }
    single<UnsavedChangesHandler> { UnsavedChangesHandlerImpl() }
}

startKoin {
    androidContext(this@PharmacyApplication)
    modules(androidPlatformModule, appModule())
}
```

Same shape in `MainViewController.kt` (iOS), `Main.kt` (JVM desktop) and
`Main.kt` (wasm). The shared `appModule()` is **identical on every platform** —
all divergence lives in `<plat>PlatformModule`.

Each entry point also does its cold-start locale bootstrap (a safety net for
the few M3 built-ins that read `Locale.current`); the live locale switch is
handled by `AppLocaleProvider`, not here.

## 4. Use it from common code

```kotlin
class ExportCsvUseCase(
    private val downloader: FileDownloader,
    dispatchers: AppDispatchers,
) : BaseUseCase<ExportCsvParam, String>(dispatchers) {
    override suspend fun execute(param: ExportCsvParam): String =
        downloader.save("export_${param.label}.csv", MimeType.Csv, param.bytes).getOrThrow()
}
```

Common code never names `Context`, `NSDocumentDirectory`, `Paths`, `Blob` — only
the interface. And it never touches `Dispatchers.IO` directly: take
`AppDispatchers` and use `dispatchers.io`.

## 5. Adding a new platform capability

1. Interface in `:core:common/common/platform/<X>.kt`.
2. `<X>Impl` in **all four** `composeApp/src/<plat>Main/…/platform/` — a missing
   one only fails when that target compiles, so run the full sweep.
3. `single<X> { XImpl(...) }` in all four entry points.
4. Inject it into a use case (or, for pure-UI concerns like
   `PointerPreferences`, read it where the theme is built).
5. Fake it in `commonTest`; never depend on a real platform impl in a unit test.

```kotlin
class FakeFileDownloader(private val fails: Boolean = false) : FileDownloader {
    var lastFilename: String? = null
    override suspend fun save(filename: String, mimeType: String, bytes: ByteArray): Result<String> {
        if (fails) return Result.failure(StorageException())
        lastFilename = filename
        return Result.success(filename)
    }
}
```

## 6. Verify

```bash
./gradlew :composeApp:auditArchitecture \
          :composeApp:assembleDebug \
          :composeApp:compileTestKotlinIosSimulatorArm64 \
          :composeApp:compileTestKotlinWasmJs \
          :composeApp:testDebugUnitTest
```

Compiling only one target is how a missing impl reaches `develop`.

## 7. Anti-patterns

- Any `expect` declaration → A27.
- A platform source folder in `:core:*` or `:features:*` → A26.
- Common code naming a platform type instead of an interface.
- `Dispatchers.IO` used directly from common code.
- An impl added to three platforms out of four.
- A binding added to the shared `appModule()` instead of the platform module.
- A test depending on a real platform impl.
