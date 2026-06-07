package app.devper.pharm.domain.repository

import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.LocalePreference
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.model.UiPreferences
import kotlinx.coroutines.flow.StateFlow

interface UiPreferencesRepository {
    val state: StateFlow<UiPreferences>
    fun setTheme(theme: ThemePreference)
    fun setFontSize(size: FontSizePreference)
    fun setDensity(density: DensityPreference)
    fun setLocale(locale: LocalePreference)
}
