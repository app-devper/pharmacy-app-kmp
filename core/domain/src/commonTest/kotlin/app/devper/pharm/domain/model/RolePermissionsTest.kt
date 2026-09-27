package app.devper.pharm.domain.model

import kotlin.test.Test
import kotlin.test.assertEquals

class RolePermissionsTest {

    // Mirrors pharmacy-api routes/permissions_test.go (ADR-0004).
    @Test
    fun permissions_follow_the_shared_role_policy() {
        val expected = mapOf(
            Role.USER to RolePermissions(canEditDrugs = false, canManageStock = false, canVoidSales = false, canEditCustomers = false),
            Role.MANAGER to RolePermissions(canEditDrugs = false, canManageStock = true, canVoidSales = false, canEditCustomers = true),
            Role.ADMIN to RolePermissions.Full,
            Role.SUPER to RolePermissions.Full,
            Role.UNKNOWN to RolePermissions(canEditDrugs = false, canManageStock = false, canVoidSales = false, canEditCustomers = false),
        )
        for ((role, permissions) in expected) {
            assertEquals(permissions, role.permissions(), "$role")
        }
    }
}
