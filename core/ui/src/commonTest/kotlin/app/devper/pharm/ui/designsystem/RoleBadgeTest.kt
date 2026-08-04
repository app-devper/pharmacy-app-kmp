package app.devper.pharm.ui.designsystem

import app.devper.pharm.domain.model.Role
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class RoleBadgeTest {

    @Test
    fun eachRoleKeepsItsOwnColourExceptTheTwoThatShareGray() {
        assertEquals(PharmBadgeTone.Purple, Role.SUPER.tone())
        assertEquals(PharmBadgeTone.Blue, Role.ADMIN.tone())
        assertEquals(PharmBadgeTone.Indigo, Role.MANAGER.tone())
        assertEquals(PharmBadgeTone.Gray, Role.USER.tone())
        assertEquals(PharmBadgeTone.Gray, Role.UNKNOWN.tone())
    }

    @Test
    fun roleLabelsAreTranslated() {
        Role.entries.forEach { role ->
            assertNotEquals("", role.label(PharmStringsTh))
            assertNotEquals("", role.label(PharmStringsEn))
        }
        assertNotEquals(Role.ADMIN.label(PharmStringsTh), Role.ADMIN.label(PharmStringsEn))
    }
}
