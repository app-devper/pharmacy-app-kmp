package app.devper.pharm.platform

import app.devper.pharm.common.platform.PointerPreferences
import kotlinx.browser.window

class PointerPreferencesImpl : PointerPreferences {
    override val isTouchPrimary: Boolean
        get() = window.matchMedia("(pointer: coarse)").matches
}
