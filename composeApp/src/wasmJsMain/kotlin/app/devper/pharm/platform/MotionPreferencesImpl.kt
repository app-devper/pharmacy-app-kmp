package app.devper.pharm.platform

import app.devper.pharm.common.platform.MotionPreferences
import kotlinx.browser.window

class MotionPreferencesImpl : MotionPreferences {
    override val reduceMotion: Boolean
        get() = window.matchMedia("(prefers-reduced-motion: reduce)").matches
}
