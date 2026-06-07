package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.model.UiPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FakeUiPreferencesRepository(
    initial: UiPreferences = UiPreferences.Default,
) : UiPreferencesRepository {

    private val internal = MutableStateFlow(initial)
    override val state: StateFlow<UiPreferences> = internal.asStateFlow()

    var lastTheme: ThemePreference? = null
        private set
    var lastFontSize: FontSizePreference? = null
        private set
    var lastDensity: DensityPreference? = null
        private set
    var lastLocale: LocalePreference? = null
        private set

    override fun setTheme(theme: ThemePreference) {
        lastTheme = theme
        internal.value = internal.value.copy(theme = theme)
    }

    override fun setFontSize(size: FontSizePreference) {
        lastFontSize = size
        internal.value = internal.value.copy(fontSize = size)
    }

    override fun setDensity(density: DensityPreference) {
        lastDensity = density
        internal.value = internal.value.copy(density = density)
    }

    override fun setLocale(locale: LocalePreference) {
        lastLocale = locale
        internal.value = internal.value.copy(locale = locale)
    }
}
