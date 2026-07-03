package app.devper.pharm.platform

import app.devper.pharm.common.platform.MotionPreferences
import platform.UIKit.UIAccessibilityIsReduceMotionEnabled

class MotionPreferencesImpl : MotionPreferences {
    override val reduceMotion: Boolean
        get() = UIAccessibilityIsReduceMotionEnabled()
}
