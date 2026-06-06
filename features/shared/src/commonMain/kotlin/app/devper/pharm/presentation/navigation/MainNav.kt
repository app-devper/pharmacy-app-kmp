package app.devper.pharm.presentation.navigation

import androidx.compose.runtime.staticCompositionLocalOf
import app.devper.pharm.ui.components.NavItem

data class MainNavConfig(
    val items: List<NavItem> = emptyList(),
    val routeForKey: (String) -> Any? = { null },
)

val LocalMainNav = staticCompositionLocalOf { MainNavConfig() }
