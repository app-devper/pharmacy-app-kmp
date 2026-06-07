@file:OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)

package app.devper.pharm.domain.usecase

import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.KySettings
import app.devper.pharm.domain.model.PharmacistInfo
import app.devper.pharm.domain.model.Settings
import app.devper.pharm.domain.model.StoreInfo
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.model.UiPreferences
import app.devper.pharm.domain.param.ReceiptSettingsInput
import app.devper.pharm.domain.param.StockSettingsInput
import app.devper.pharm.domain.param.UpdateSettingsParam
import app.devper.pharm.domain.repository.FakeSettingsRepository
import app.devper.pharm.domain.repository.FakeUiPreferencesRepository
import app.devper.pharm.domain.testDispatchers
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private fun param() = UpdateSettingsParam(
    store = StoreInfo(name = "Devper Pharmacy"),
    receipt = ReceiptSettingsInput(header = "Hello", footer = "Thanks"),
    stock = StockSettingsInput(lowStockThreshold = 5),
    pharmacist = PharmacistInfo(name = "Pharm A"),
    ky = KySettings(),
    timezone = "Asia/Bangkok",
)

class SetThemePreferenceUseCaseTest {

    @Test
    fun light_theme_persisted_in_repository() {
        val repo = FakeUiPreferencesRepository()

        SetThemePreferenceUseCase(repo).invoke(ThemePreference.Light)

        assertEquals(ThemePreference.Light, repo.lastTheme)
        assertEquals(ThemePreference.Light, repo.state.value.theme)
    }

    @Test
    fun dark_theme_persisted_in_repository() {
        val repo = FakeUiPreferencesRepository()

        SetThemePreferenceUseCase(repo).invoke(ThemePreference.Dark)

        assertEquals(ThemePreference.Dark, repo.state.value.theme)
    }

    @Test
    fun auto_theme_resets_after_explicit() {
        val repo = FakeUiPreferencesRepository(initial = UiPreferences(theme = ThemePreference.Dark))

        SetThemePreferenceUseCase(repo).invoke(ThemePreference.Auto)

        assertEquals(ThemePreference.Auto, repo.state.value.theme)
    }
}

class SetFontSizePreferenceUseCaseTest {

    @Test
    fun font_size_persisted_in_repository() {
        val repo = FakeUiPreferencesRepository()

        SetFontSizePreferenceUseCase(repo).invoke(FontSizePreference.Lg)

        assertEquals(FontSizePreference.Lg, repo.lastFontSize)
        assertEquals(FontSizePreference.Lg, repo.state.value.fontSize)
    }

    @Test
    fun extra_large_font_persists() {
        val repo = FakeUiPreferencesRepository()

        SetFontSizePreferenceUseCase(repo).invoke(FontSizePreference.Xl)

        assertEquals(FontSizePreference.Xl, repo.state.value.fontSize)
    }
}

class SetDensityPreferenceUseCaseTest {

    @Test
    fun compact_density_persisted_in_repository() {
        val repo = FakeUiPreferencesRepository()

        SetDensityPreferenceUseCase(repo).invoke(DensityPreference.Compact)

        assertEquals(DensityPreference.Compact, repo.lastDensity)
        assertEquals(DensityPreference.Compact, repo.state.value.density)
    }

    @Test
    fun comfortable_density_persisted_in_repository() {
        val repo = FakeUiPreferencesRepository(initial = UiPreferences(density = DensityPreference.Compact))

        SetDensityPreferenceUseCase(repo).invoke(DensityPreference.Comfortable)

        assertEquals(DensityPreference.Comfortable, repo.state.value.density)
    }
}

class UpdateSettingsUseCaseTest {

    @Test
    fun update_forwards_param_to_repository() = runTest {
        val repo = FakeSettingsRepository()

        val result = UpdateSettingsUseCase(repo, testDispatchers()).invoke(param())

        assertTrue(result.isSuccess)
        assertEquals(param(), repo.lastUpdate)
    }

    @Test
    fun update_returns_repository_settings() = runTest {
        val seeded = Settings(store = StoreInfo(name = "Already saved"))
        val repo = FakeSettingsRepository(initialSettings = seeded)

        val result = UpdateSettingsUseCase(repo, testDispatchers()).invoke(param())

        assertEquals(seeded, result.getOrNull())
    }
}

class RefreshSettingsUseCaseTest {

    @Test
    fun refresh_calls_repository_and_returns_settings() = runTest {
        val seeded = Settings(store = StoreInfo(name = "Pulled fresh"))
        val repo = FakeSettingsRepository(initialSettings = seeded)

        val result = RefreshSettingsUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isSuccess)
        assertEquals(seeded, result.getOrNull())
        assertEquals(1, repo.refreshCallCount)
    }

    @Test
    fun refresh_failure_wraps_in_result() = runTest {
        val repo = FakeSettingsRepository(refreshThrows = true)

        val result = RefreshSettingsUseCase(repo, testDispatchers()).invoke()

        assertTrue(result.isFailure)
        assertEquals(1, repo.refreshCallCount)
    }
}
