package app.devper.pharm.presentation.users

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import kotlin.test.Test
import kotlin.test.assertEquals

class RoleLabelTest {
    @Test
    fun mapsAllRolesToEnglishLabels() {
        assertEquals("Super Admin", Role.SUPER.label(PharmStringsEn))
        assertEquals("Admin", Role.ADMIN.label(PharmStringsEn))
        assertEquals("Manager", Role.MANAGER.label(PharmStringsEn))
        assertEquals("User", Role.USER.label(PharmStringsEn))
        assertEquals("Unknown", Role.UNKNOWN.label(PharmStringsEn))
    }

    @Test
    fun mapsAllRolesToThaiLabels() {
        assertEquals("ผู้ดูแลสูงสุด", Role.SUPER.label(PharmStringsTh))
        assertEquals("ผู้ดูแลระบบ", Role.ADMIN.label(PharmStringsTh))
        assertEquals("ผู้จัดการ", Role.MANAGER.label(PharmStringsTh))
        assertEquals("ผู้ใช้งาน", Role.USER.label(PharmStringsTh))
        assertEquals("ไม่ระบุ", Role.UNKNOWN.label(PharmStringsTh))
    }
}
