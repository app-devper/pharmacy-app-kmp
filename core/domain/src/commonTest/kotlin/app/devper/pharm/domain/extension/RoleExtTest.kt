package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class RoleExtTest {

    @Test
    fun can_view_users_helper_includes_manager() {
        assertEquals(true, Role.SUPER.canViewUsers())
        assertEquals(true, Role.ADMIN.canViewUsers())
        assertEquals(true, Role.MANAGER.canViewUsers())
        assertFalse(Role.USER.canViewUsers())
        assertFalse(Role.UNKNOWN.canViewUsers())
    }

    @Test
    fun atLeast_follows_user_manager_admin_super_order() {
        val order = listOf(Role.USER, Role.MANAGER, Role.ADMIN, Role.SUPER)
        for ((i, role) in order.withIndex()) {
            for ((j, min) in order.withIndex()) {
                assertEquals(i >= j, role.atLeast(min), "$role atLeast $min")
            }
        }
    }

    @Test
    fun unknown_role_meets_no_minimum() {
        for (min in listOf(Role.USER, Role.MANAGER, Role.ADMIN, Role.SUPER)) {
            assertFalse(Role.UNKNOWN.atLeast(min))
        }
    }
}
