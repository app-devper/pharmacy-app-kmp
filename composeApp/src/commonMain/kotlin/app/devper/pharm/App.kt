package app.devper.pharm

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import app.devper.pharm.domain.model.DensityPreference
import app.devper.pharm.domain.model.ThemePreference
import app.devper.pharm.presentation.AppViewModel
import app.devper.pharm.presentation.navigation.AppNavHost
import app.devper.pharm.ui.components.LocalSidebarState
import app.devper.pharm.ui.components.SidebarState
import app.devper.pharm.ui.designsystem.LocalPharmDensity
import app.devper.pharm.ui.designsystem.PharmDensity
import app.devper.pharm.ui.common.LocalPharmSnackbar
import app.devper.pharm.ui.common.PharmSnackbarHost
import app.devper.pharm.ui.common.PharmSnackbarHostUi
import app.devper.pharm.ui.theme.LocalThemeController
import app.devper.pharm.ui.theme.PharmacyTheme
import app.devper.pharm.ui.theme.ThemeController
import androidx.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
@Preview
fun App(viewModel: AppViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
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
    val snackbarHost = remember { PharmSnackbarHost() }
    val density = when (state.uiPreferences.density) {
        DensityPreference.Comfortable -> PharmDensity.Comfortable
        DensityPreference.Compact     -> PharmDensity.Compact
    }
    var sidebarCollapsed by remember { mutableStateOf(false) }
    val sidebarState = remember(sidebarCollapsed) {
        SidebarState(
            collapsed = sidebarCollapsed,
            canCollapse = true,
            toggle = { sidebarCollapsed = !sidebarCollapsed },
        )
    }
    PharmacyTheme(
        darkTheme = darkTheme,
        fontScale = state.uiPreferences.fontSize.scale,
    ) {
        CompositionLocalProvider(
            LocalThemeController provides themeController,
            LocalPharmDensity provides density,
            LocalSidebarState provides sidebarState,
            LocalPharmSnackbar provides snackbarHost,
        ) {
            Surface {
                Box(modifier = Modifier.fillMaxSize()) {
                    AppNavHost(viewModel = viewModel)
                    PharmSnackbarHostUi(
                        host = snackbarHost,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
