package app.devper.pharm.platform

import app.devper.pharm.common.platform.InputPreferences
import kotlinx.browser.window

class InputPreferencesImpl : InputPreferences {
    override val isTouchPrimary: Boolean
        get() = window.matchMedia("(pointer: coarse)").matches
}
