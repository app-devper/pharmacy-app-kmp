package app.devper.pharm.presentation.settings

import androidx.compose.ui.unit.dp
import app.devper.pharm.ui.components.PharmBreakpoint
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

    @Test
    fun splitThresholdFollowsTheSharedBreakpoint() {
        assertEquals(SettingsDialogLayout.Compact, settingsDialogLayout(PharmBreakpoint.FormThreeCol - 1.dp))
        assertEquals(SettingsDialogLayout.Split, settingsDialogLayout(PharmBreakpoint.FormThreeCol))
    }
}
