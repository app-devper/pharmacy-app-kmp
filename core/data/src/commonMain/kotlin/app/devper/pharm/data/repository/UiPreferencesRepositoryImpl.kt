package app.devper.pharm.data.repository

import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.model.UiPreferences
import app.devper.pharm.domain.repository.UiPreferencesRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class UiPreferencesRepositoryImpl(private val settings: Settings) : UiPreferencesRepository {

    private val internal = MutableStateFlow(load())
    override val state: StateFlow<UiPreferences> = internal.asStateFlow()

    override fun setTheme(theme: ThemePreference) {
        settings.putString(KEY_THEME, theme.wire)
        internal.value = internal.value.copy(theme = theme)
    }

    override fun setFontSize(size: FontSizePreference) {
        settings.putString(KEY_FONT_SIZE, size.wire)
        internal.value = internal.value.copy(fontSize = size)
    }

    private fun load(): UiPreferences = UiPreferences(
        theme = ThemePreference.parse(settings.getStringOrNull(KEY_THEME)),
        fontSize = FontSizePreference.parse(settings.getStringOrNull(KEY_FONT_SIZE)),
    )

    private companion object {
        const val KEY_THEME = "ui.theme"
        const val KEY_FONT_SIZE = "ui.fontSize"
    }
}
