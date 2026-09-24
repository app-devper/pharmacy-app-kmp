package app.devper.pharm.presentation.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import app.devper.pharm.ui.i18n.PharmStrings

internal data class MainSidebarEntry(
    val target: Any,
    val icon: ImageVector,
    val label: ((PharmStrings) -> String)? = null,
    val admin: Boolean = false,
    val pinned: Boolean = false,
    val sectionLabel: (PharmStrings) -> String,
)
