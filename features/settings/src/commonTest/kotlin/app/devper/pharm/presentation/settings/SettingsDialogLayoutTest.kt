package app.devper.pharm.presentation.settings

import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class SettingsDialogLayoutTest {

    @Test
    fun phoneWidthsUseCompactTabs() {
        assertEquals(SettingsDialogLayout.Compact, settingsDialogLayout(320.dp))
        assertEquals(SettingsDialogLayout.Compact, settingsDialogLayout(360.dp))
    }

    @Test
    fun narrowTabletKeepsCompactNavigation() {
        assertEquals(SettingsDialogLayout.Compact, settingsDialogLayout(600.dp))
        assertEquals(SettingsDialogLayout.Compact, settingsDialogLayout(719.dp))
    }

    @Test
    fun wideTabletAndDesktopUseSplitNavigation() {
        assertEquals(SettingsDialogLayout.Split, settingsDialogLayout(720.dp))
        assertEquals(SettingsDialogLayout.Split, settingsDialogLayout(840.dp))
    }
}
