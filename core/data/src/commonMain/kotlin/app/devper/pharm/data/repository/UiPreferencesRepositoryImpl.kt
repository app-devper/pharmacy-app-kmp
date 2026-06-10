package app.devper.pharm.data.repository

import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.model.UiPreferences
import app.devper.pharm.domain.repository.settings.UiPreferencesRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UiPreferencesRepositoryImpl(
    private val settings: Settings,
) : UiPreferencesRepository {

    private val internal = MutableStateFlow(loadFromSettings())
    override val state: StateFlow<UiPreferences> = internal.asStateFlow()

    override fun setTheme(theme: ThemePreference) {
        internal.value = internal.value.copy(theme = theme)
        settings.putString(KEY_THEME, theme.wire)
    }

    override fun setFontSize(size: FontSizePreference) {
        internal.value = internal.value.copy(fontSize = size)
        settings.putString(KEY_FONT_SIZE, size.wire)
    }

    override fun setDensity(density: DensityPreference) {
        internal.value = internal.value.copy(density = density)
        settings.putString(KEY_DENSITY, density.wire)
    }

    override fun setLocale(locale: LocalePreference) {
        internal.value = internal.value.copy(locale = locale)
        settings.putString(KEY_LOCALE, locale.wire)
    }

    private fun loadFromSettings(): UiPreferences = UiPreferences(
        theme = ThemePreference.parse(settings.getStringOrNull(KEY_THEME)),
        fontSize = FontSizePreference.parse(settings.getStringOrNull(KEY_FONT_SIZE)),
        density = DensityPreference.parse(settings.getStringOrNull(KEY_DENSITY)),
        locale = LocalePreference.parse(settings.getStringOrNull(KEY_LOCALE)),
    )

    private companion object {
        const val KEY_THEME = "ui.theme"
        const val KEY_FONT_SIZE = "ui.fontSize"
        const val KEY_DENSITY = "ui.density"
        const val KEY_LOCALE = "ui.locale"
    }
}
