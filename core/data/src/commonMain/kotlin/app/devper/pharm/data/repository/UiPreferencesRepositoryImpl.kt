package app.devper.pharm.data.repository

import app.devper.pharm.common.AppDispatchers
import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.FontSizePreference
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.domain.model.UiPreferences
import app.devper.pharm.domain.repository.UiPreferencesRepository
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class UiPreferencesRepositoryImpl(
    private val settings: Settings,
    dispatchers: AppDispatchers,
) : UiPreferencesRepository {

    private val scope = CoroutineScope(SupervisorJob() + dispatchers.io)
    private val internal = MutableStateFlow(UiPreferences())
    override val state: StateFlow<UiPreferences> = internal.asStateFlow()

    init {
        scope.launch {
            internal.value = UiPreferences(
                theme = ThemePreference.parse(settings.getStringOrNull(KEY_THEME)),
                fontSize = FontSizePreference.parse(settings.getStringOrNull(KEY_FONT_SIZE)),
                density = DensityPreference.parse(settings.getStringOrNull(KEY_DENSITY)),
            )
        }
    }

    override fun setTheme(theme: ThemePreference) {
        internal.value = internal.value.copy(theme = theme)
        scope.launch { settings.putString(KEY_THEME, theme.wire) }
    }

    override fun setFontSize(size: FontSizePreference) {
        internal.value = internal.value.copy(fontSize = size)
        scope.launch { settings.putString(KEY_FONT_SIZE, size.wire) }
    }

    override fun setDensity(density: DensityPreference) {
        internal.value = internal.value.copy(density = density)
        scope.launch { settings.putString(KEY_DENSITY, density.wire) }
    }

    private companion object {
        const val KEY_THEME = "ui.theme"
        const val KEY_FONT_SIZE = "ui.fontSize"
        const val KEY_DENSITY = "ui.density"
    }
}
