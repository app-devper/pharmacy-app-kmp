package app.devper.pharm

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.presentation.AppViewModel
import app.devper.pharm.presentation.navigation.AppNavHost
import app.devper.pharm.ui.theme.LocalThemeController
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.ThemeController
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(viewModel: AppViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val systemDark = isSystemInDarkTheme()
    val darkTheme = when (state.uiPreferences.theme) {
        ThemePreference.Light -> false
        ThemePreference.Dark  -> true
        ThemePreference.Auto  -> systemDark
    }
    val themeController = remember(darkTheme) {
        ThemeController(
            isDark = darkTheme,
            canToggle = true,
            toggle = { viewModel.toggleTheme(darkTheme) },
        )
    }
    PharmacyTheme(
        darkTheme = darkTheme,
        fontScale = state.uiPreferences.fontSize.scale,
    ) {
        CompositionLocalProvider(LocalThemeController provides themeController) {
            Surface {
                AppNavHost(viewModel = viewModel)
            }
        }
    }
}
