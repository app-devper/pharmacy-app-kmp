package app.devper.pharm.ui.designsystem

import app.devper.pharm.ui.components.WindowSize

internal data class ToolbarActionPlacement(
    val primaryInTopbar: Boolean,
    val secondaryInTopbar: Boolean,
)

internal fun toolbarActionPlacement(
    windowSize: WindowSize,
    hasBack: Boolean,
    compactHeaderActions: Boolean = true,
): ToolbarActionPlacement {
    val usesTopbar = windowSize.isCompactShell && (!hasBack || compactHeaderActions)
    return ToolbarActionPlacement(
        primaryInTopbar = usesTopbar,
        secondaryInTopbar = usesTopbar && hasBack,
    )
}
