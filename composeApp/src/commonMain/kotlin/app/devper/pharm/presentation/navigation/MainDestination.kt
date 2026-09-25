package app.devper.pharm.presentation.navigation

import app.devper.pharm.ui.i18n.PharmStrings
import kotlin.reflect.KClass

internal data class MainDestination(
    val route: KClass<*>,
    val title: (PharmStrings) -> String,
    val section: KClass<*>?,
    val isSubPage: Boolean = false,
    val sidebar: MainSidebarEntry? = null,
) {
    val sectionKey: String? get() = section?.let(::k)
}
