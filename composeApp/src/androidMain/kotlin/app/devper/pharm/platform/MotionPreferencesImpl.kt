package app.devper.pharm.platform

import android.content.Context
import android.provider.Settings
import app.devper.pharm.common.platform.MotionPreferences

class MotionPreferencesImpl(private val context: Context) : MotionPreferences {
    override val reduceMotion: Boolean
        get() = Settings.Global.getFloat(
            context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE,
            1f,
        ) == 0f
}
