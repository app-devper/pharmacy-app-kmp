package app.devper.pharm.domain.extension

import app.devper.pharm.domain.model.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class RoleExtTest {

    @Test
    fun super_can_manage_admin_manager_user_but_not_super() {
        assertFalse(Role.SUPER.canManage(Role.SUPER, isSelf = false))
        assertEquals(true, Role.SUPER.canManage(Role.ADMIN, isSelf = false))
        assertEquals(true, Role.SUPER.canManage(Role.MANAGER, isSelf = false))
        assertEquals(true, Role.SUPER.canManage(Role.USER, isSelf = false))
    }

    @Test
    fun admin_can_manage_manager_and_user_only() {
        assertFalse(Role.ADMIN.canManage(Role.SUPER, isSelf = false))
        assertFalse(Role.ADMIN.canManage(Role.ADMIN, isSelf = false))
        assertEquals(true, Role.ADMIN.canManage(Role.MANAGER, isSelf = false))
        assertEquals(true, Role.ADMIN.canManage(Role.USER, isSelf = false))
    }

    @Test
    fun manager_cannot_manage_anyone() {
        assertFalse(Role.MANAGER.canManage(Role.SUPER, isSelf = false))
        assertFalse(Role.MANAGER.canManage(Role.ADMIN, isSelf = false))
        assertFalse(Role.MANAGER.canManage(Role.MANAGER, isSelf = false))
        assertFalse(Role.MANAGER.canManage(Role.USER, isSelf = false))
    }

    @Test
    fun user_cannot_manage_anyone() {
        assertFalse(Role.USER.canManage(Role.SUPER, isSelf = false))
        assertFalse(Role.USER.canManage(Role.ADMIN, isSelf = false))
        assertFalse(Role.USER.canManage(Role.MANAGER, isSelf = false))
        assertFalse(Role.USER.canManage(Role.USER, isSelf = false))
    }

    @Test
    fun unknown_actor_cannot_manage_anyone() {
        assertFalse(Role.UNKNOWN.canManage(Role.SUPER, isSelf = false))
        assertFalse(Role.UNKNOWN.canManage(Role.ADMIN, isSelf = false))
        assertFalse(Role.UNKNOWN.canManage(Role.MANAGER, isSelf = false))
        assertFalse(Role.UNKNOWN.canManage(Role.USER, isSelf = false))
    }

    @Test
    fun is_self_always_returns_false_even_for_super_managing_user() {
        assertFalse(Role.SUPER.canManage(Role.USER, isSelf = true))
        assertFalse(Role.SUPER.canManage(Role.ADMIN, isSelf = true))
        assertFalse(Role.ADMIN.canManage(Role.USER, isSelf = true))
        assertFalse(Role.MANAGER.canManage(Role.USER, isSelf = true))
    }

    @Test
    fun can_manage_users_helper_reflects_super_and_admin() {
        assertEquals(true, Role.SUPER.canManageUsers())
        assertEquals(true, Role.ADMIN.canManageUsers())
        assertFalse(Role.MANAGER.canManageUsers())
        assertFalse(Role.USER.canManageUsers())
        assertFalse(Role.UNKNOWN.canManageUsers())
    }

    @Test
    fun can_view_users_helper_includes_manager() {
        assertEquals(true, Role.SUPER.canViewUsers())
        assertEquals(true, Role.ADMIN.canViewUsers())
        assertEquals(true, Role.MANAGER.canViewUsers())
        assertFalse(Role.USER.canViewUsers())
        assertFalse(Role.UNKNOWN.canViewUsers())
    }
}
