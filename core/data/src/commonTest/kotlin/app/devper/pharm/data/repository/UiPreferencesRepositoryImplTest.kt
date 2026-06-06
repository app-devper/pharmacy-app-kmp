package app.devper.pharm.data.repository

import app.devper.pharm.data.storage.MemorySettings
import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.ThemePreference
import com.russhwolf.settings.Settings
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UiPreferencesRepositoryImplTest {

    @Test
    fun fresh_settings_yields_defaults() = runTest {
        val settings = MemorySettings()
        val repo = UiPreferencesRepositoryImpl(settings)

        val state = repo.state.value
        assertEquals(ThemePreference.Auto, state.theme)
        assertEquals(FontSizePreference.Md, state.fontSize)
    }

    @Test
    fun previously_persisted_values_load_into_state() = runTest {
        val settings = MemorySettings().apply {
            putString("ui.theme", ThemePreference.Dark.wire)
            putString("ui.fontSize", FontSizePreference.Lg.wire)
        }
        val repo = UiPreferencesRepositoryImpl(settings)

        val state = repo.state.value
        assertEquals(ThemePreference.Dark, state.theme)
        assertEquals(FontSizePreference.Lg, state.fontSize)
    }

    @Test
    fun setTheme_updates_state_synchronously_and_persists() = runTest {
        val settings = MemorySettings()
        val repo = UiPreferencesRepositoryImpl(settings)

        repo.setTheme(ThemePreference.Dark)

        assertEquals(ThemePreference.Dark, repo.state.value.theme)
        assertEquals(ThemePreference.Dark.wire, settings.getStringOrNull("ui.theme"))
    }

    @Test
    fun setFontSize_updates_state_synchronously_and_persists() = runTest {
        val settings = MemorySettings()
        val repo = UiPreferencesRepositoryImpl(settings)

        repo.setFontSize(FontSizePreference.Xl)

        assertEquals(FontSizePreference.Xl, repo.state.value.fontSize)
        assertEquals(FontSizePreference.Xl.wire, settings.getStringOrNull("ui.fontSize"))
    }

    @Test
    fun setDensity_updates_state_synchronously_and_persists() = runTest {
        val settings = MemorySettings()
        val repo = UiPreferencesRepositoryImpl(settings)

        repo.setDensity(DensityPreference.Compact)

        assertEquals(DensityPreference.Compact, repo.state.value.density)
        assertEquals(DensityPreference.Compact.wire, settings.getStringOrNull("ui.density"))
    }

    @Test
    fun fresh_settings_yields_comfortable_density_default() = runTest {
        val settings = MemorySettings()
        val repo = UiPreferencesRepositoryImpl(settings)

        assertEquals(DensityPreference.Comfortable, repo.state.value.density)
    }

    @Test
    fun setTheme_does_not_affect_font_size_or_vice_versa() = runTest {
        val settings = MemorySettings()
        val repo = UiPreferencesRepositoryImpl(settings)

        repo.setTheme(ThemePreference.Light)
        repo.setFontSize(FontSizePreference.Sm)

        assertEquals(ThemePreference.Light, repo.state.value.theme)
        assertEquals(FontSizePreference.Sm, repo.state.value.fontSize)
    }

    @Test
    fun a_second_repo_built_against_same_settings_replays_writes() = runTest {
        val settings: Settings = MemorySettings()
        val first = UiPreferencesRepositoryImpl(settings)
        first.setTheme(ThemePreference.Dark)
        first.setFontSize(FontSizePreference.Xl)

        val second = UiPreferencesRepositoryImpl(settings)

        assertEquals(ThemePreference.Dark, second.state.value.theme)
        assertEquals(FontSizePreference.Xl, second.state.value.fontSize)
    }

    @Test
    fun unknown_wire_value_falls_back_to_defaults() = runTest {
        val settings = MemorySettings().apply {
            putString("ui.theme", "neon-cyber-glow")
            putString("ui.fontSize", "xxxl")
        }
        val repo = UiPreferencesRepositoryImpl(settings)

        assertEquals(ThemePreference.Auto, repo.state.value.theme)
        assertEquals(FontSizePreference.Md, repo.state.value.fontSize)
    }

    @Test
    fun cleared_settings_have_no_persisted_keys_initially() {
        val settings = MemorySettings()
        assertNull(settings.getStringOrNull("ui.theme"))
        assertNull(settings.getStringOrNull("ui.fontSize"))
    }
}
