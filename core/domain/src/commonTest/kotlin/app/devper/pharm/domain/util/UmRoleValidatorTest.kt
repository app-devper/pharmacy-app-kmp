package app.devper.pharm.domain.util

import app.devper.pharm.domain.model.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class UmRoleValidatorTest {

    @Test
    fun super_can_manage_admin_manager_user_but_not_super() {
        assertFalse(UmRoleValidator.canManage(Role.SUPER, Role.SUPER, isSelf = false))
        assertEquals(true, UmRoleValidator.canManage(Role.SUPER, Role.ADMIN, isSelf = false))
        assertEquals(true, UmRoleValidator.canManage(Role.SUPER, Role.MANAGER, isSelf = false))
        assertEquals(true, UmRoleValidator.canManage(Role.SUPER, Role.USER, isSelf = false))
    }

    @Test
    fun admin_can_manage_manager_and_user_only() {
        assertFalse(UmRoleValidator.canManage(Role.ADMIN, Role.SUPER, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.ADMIN, Role.ADMIN, isSelf = false))
        assertEquals(true, UmRoleValidator.canManage(Role.ADMIN, Role.MANAGER, isSelf = false))
        assertEquals(true, UmRoleValidator.canManage(Role.ADMIN, Role.USER, isSelf = false))
    }

    @Test
    fun manager_cannot_manage_anyone() {
        assertFalse(UmRoleValidator.canManage(Role.MANAGER, Role.SUPER, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.MANAGER, Role.ADMIN, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.MANAGER, Role.MANAGER, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.MANAGER, Role.USER, isSelf = false))
    }

    @Test
    fun user_cannot_manage_anyone() {
        assertFalse(UmRoleValidator.canManage(Role.USER, Role.SUPER, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.USER, Role.ADMIN, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.USER, Role.MANAGER, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.USER, Role.USER, isSelf = false))
    }

    @Test
    fun unknown_actor_cannot_manage_anyone() {
        assertFalse(UmRoleValidator.canManage(Role.UNKNOWN, Role.SUPER, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.UNKNOWN, Role.ADMIN, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.UNKNOWN, Role.MANAGER, isSelf = false))
        assertFalse(UmRoleValidator.canManage(Role.UNKNOWN, Role.USER, isSelf = false))
    }

    @Test
    fun is_self_always_returns_false_even_for_super_managing_user() {
        assertFalse(UmRoleValidator.canManage(Role.SUPER, Role.USER, isSelf = true))
        assertFalse(UmRoleValidator.canManage(Role.SUPER, Role.ADMIN, isSelf = true))
        assertFalse(UmRoleValidator.canManage(Role.ADMIN, Role.USER, isSelf = true))
        assertFalse(UmRoleValidator.canManage(Role.MANAGER, Role.USER, isSelf = true))
    }

    @Test
    fun can_manage_users_helper_reflects_super_and_admin() {
        assertEquals(true, UmRoleValidator.canManageUsers(Role.SUPER))
        assertEquals(true, UmRoleValidator.canManageUsers(Role.ADMIN))
        assertFalse(UmRoleValidator.canManageUsers(Role.MANAGER))
        assertFalse(UmRoleValidator.canManageUsers(Role.USER))
        assertFalse(UmRoleValidator.canManageUsers(Role.UNKNOWN))
    }

    @Test
    fun can_view_users_helper_includes_manager() {
        assertEquals(true, UmRoleValidator.canViewUsers(Role.SUPER))
        assertEquals(true, UmRoleValidator.canViewUsers(Role.ADMIN))
        assertEquals(true, UmRoleValidator.canViewUsers(Role.MANAGER))
        assertFalse(UmRoleValidator.canViewUsers(Role.USER))
        assertFalse(UmRoleValidator.canViewUsers(Role.UNKNOWN))
    }
}
