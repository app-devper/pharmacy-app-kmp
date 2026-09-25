package app.devper.pharm.presentation.navigation

import app.devper.pharm.domain.model.Role
import androidx.compose.ui.graphics.vector.ImageVector
import app.devper.pharm.ui.i18n.PharmStrings

internal data class MainSidebarEntry(
    val target: Any,
    val icon: ImageVector,
    val label: ((PharmStrings) -> String)? = null,
    val minRole: Role = Role.USER,
    val pinned: Boolean = false,
    val sectionLabel: (PharmStrings) -> String,
)
