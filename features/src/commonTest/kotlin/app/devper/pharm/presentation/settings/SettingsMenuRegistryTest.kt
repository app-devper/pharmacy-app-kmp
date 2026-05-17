package app.devper.pharm.presentation.settings

import app.devper.pharm.domain.model.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsMenuRegistryTest {

    @Test
    fun super_sees_users_and_bulk_import() {
        val keys = SettingsMenuRegistry.groupsFor(Role.SUPER)
            .flatMap { (_, items) -> items.map { it.key } }
            .toSet()
        assertTrue(SettingsMenuKey.Users in keys)
        assertTrue(SettingsMenuKey.BulkImport in keys)
        assertTrue(SettingsMenuKey.Profile in keys)
    }

    @Test
    fun admin_sees_users_and_bulk_import() {
        val keys = SettingsMenuRegistry.groupsFor(Role.ADMIN)
            .flatMap { (_, items) -> items.map { it.key } }
            .toSet()
        assertTrue(SettingsMenuKey.Users in keys)
        assertTrue(SettingsMenuKey.BulkImport in keys)
    }

    @Test
    fun manager_can_view_users_but_not_bulk_import() {
        val keys = SettingsMenuRegistry.groupsFor(Role.MANAGER)
            .flatMap { (_, items) -> items.map { it.key } }
            .toSet()
        assertTrue(SettingsMenuKey.Users in keys)
        assertFalse(SettingsMenuKey.BulkImport in keys)
    }

    @Test
    fun user_role_cannot_see_users_or_bulk_import() {
        val keys = SettingsMenuRegistry.groupsFor(Role.USER)
            .flatMap { (_, items) -> items.map { it.key } }
            .toSet()
        assertFalse(SettingsMenuKey.Users in keys)
        assertFalse(SettingsMenuKey.BulkImport in keys)
        assertTrue(SettingsMenuKey.Profile in keys)
        assertTrue(SettingsMenuKey.Reports in keys)
        assertTrue(SettingsMenuKey.Help in keys)
    }

    @Test
    fun unknown_role_still_sees_profile_and_help_but_no_admin_links() {
        val keys = SettingsMenuRegistry.groupsFor(Role.UNKNOWN)
            .flatMap { (_, items) -> items.map { it.key } }
            .toSet()
        assertTrue(SettingsMenuKey.Profile in keys)
        assertTrue(SettingsMenuKey.Help in keys)
        assertFalse(SettingsMenuKey.Users in keys)
        assertFalse(SettingsMenuKey.BulkImport in keys)
    }

    @Test
    fun groups_are_in_canonical_order() {
        val groups = SettingsMenuRegistry.groupsFor(Role.SUPER).map { it.first }
        assertEquals(
            listOf(
                SettingsMenuGroup.Self,
                SettingsMenuGroup.Inventory,
                SettingsMenuGroup.Compliance,
                SettingsMenuGroup.Reports,
                SettingsMenuGroup.Admin,
                SettingsMenuGroup.Help,
            ),
            groups,
        )
    }
}
