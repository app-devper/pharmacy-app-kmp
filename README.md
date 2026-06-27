# PharmacyApp — Kotlin Multiplatform companion

KMP / Compose Multiplatform port of the React `frontend/` web POS. One Kotlin codebase ships to Android, iOS, Desktop (JVM), and Web (wasmJs), sharing the same `pharmacy-app/backend` and `um-api` auth.

## Stack

| Concern | Choice |
|---|---|
| Language | Kotlin 2.3.21 |
| UI | Compose Multiplatform 1.11.0 |
| Networking | Ktor 3.5.0 |
| DI | Koin 4.2.1 |
| Navigation | androidx.navigation-compose 2.9.2 |
| Storage | multiplatform-settings 1.3.0 |
| Build | Gradle 8.14.3 · AGP 8.13.2 |

## Run

```bash
# Full check
./gradlew :composeApp:check

# Per-platform targets
./gradlew :composeApp:assembleDebug                 # Android
./gradlew :composeApp:run                           # Desktop (JVM)
./gradlew :composeApp:wasmJsBrowserDevelopmentRun   # Web
./gradlew :composeApp:iosSimulatorArm64Test         # iOS sim

# Architecture audit only
./gradlew :composeApp:auditArchitecture
```

For iOS as a real app: open `iosApp/iosApp.xcodeproj` in Xcode.

## Backends

| Service | URL |
|---|---|
| um-api (auth) | `https://devper-um-1056670356976.asia-southeast1.run.app` |
| pharmacy-app/backend | `https://pharmacy-api-1056670356976.asia-southeast1.run.app` |

To point at a local backend, override `ApiConfig` in the platform Koin module.

## Docs

- [`CLAUDE.md`](CLAUDE.md) — project conventions, module rules, verify command
- [`MODULE_GRAPH.md`](MODULE_GRAPH.md) — full 26-module dep matrix, what lives where, per-feature recipe
