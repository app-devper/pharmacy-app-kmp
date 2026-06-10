package app.devper.pharm.presentation.settings.i18n

import app.devper.pharm.common.NetworkException
import app.devper.pharm.presentation.settings.exception.SettingsUiStateError
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import app.devper.pharm.ui.i18n.localizeCommon
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class SettingsUiStateErrorLocalizeTest {

    @Test
    fun feature_error_maps_to_its_own_string_in_both_languages() {
        val error = SettingsUiStateError.LoadSettingsFailed()
        assertEquals(PharmStringsTh.settingsLoadFailed, error.localizeSettings(PharmStringsTh))
        assertEquals(PharmStringsEn.settingsLoadFailed, error.localizeSettings(PharmStringsEn))
        assertTrue(error.localizeSettings(PharmStringsTh).isNotBlank())
        assertNotEquals(error.localizeSettings(PharmStringsTh), error.localizeSettings(PharmStringsEn))
    }

    @Test
    fun unmapped_exception_delegates_to_localizeCommon() {
        val transport = NetworkException()
        assertEquals(transport.localizeCommon(PharmStringsTh), transport.localizeSettings(PharmStringsTh))
        assertEquals(PharmStringsEn.commonErrorNetwork, transport.localizeSettings(PharmStringsEn))
    }
}
