package app.devper.pharm.presentation.navigation

import app.devper.pharm.presentation.stock.DrugEdit
import app.devper.pharm.presentation.stock.Stock
import app.devper.pharm.presentation.profile.Profile
import app.devper.pharm.presentation.sell.Sell
import app.devper.pharm.presentation.ky.Ky10
import app.devper.pharm.presentation.ky.Ky9
import app.devper.pharm.presentation.settings.Settings
import app.devper.pharm.ui.i18n.PharmStringsEn
import app.devper.pharm.ui.i18n.PharmStringsTh
import app.devper.pharm.domain.model.Role
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class MainDestinationTest {
    @Test
    fun destinations_are_unique_and_every_section_resolves_to_a_sidebar_target() {
        assertEquals(mainDestinations.size, mainDestinations.map { it.route }.distinct().size)
        mainDestinations.forEach { destination ->
            destination.sectionKey?.let { assertNotNull(routeForKey(it)) }
            destination.sidebar?.let { assertEquals(destination.route, it.target::class) }
        }
    }

    @Test
    fun parameterized_edit_routes_keep_stock_section_and_subpage_classification() {
        val destination = assertNotNull(destinationFor(k(DrugEdit::class) + "/drug-id?mode=edit"))
        assertEquals(k(Stock::class), destination.sectionKey)
        assertTrue(destination.isSubPage)
        assertEquals(PharmStringsEn.navStock, destination.title(PharmStringsEn))
        assertFalse(assertNotNull(destinationFor(k(Stock::class))).isSubPage)
        assertNull(routeForKey(k(DrugEdit::class)))
    }

    @Test
    fun profile_has_back_navigation_without_highlighting_a_section() {
        val destination = assertNotNull(destinationFor(k(Profile::class)))
        assertTrue(destination.isSubPage)
        assertNull(destination.sectionKey)
    }

    @Test
    fun ky_forms_share_sidebar_section_without_becoming_subpages() {
        val destination = assertNotNull(destinationFor(k(Ky10::class)))
        assertEquals(k(Ky9::class), destination.sectionKey)
        assertFalse(destination.isSubPage)
    }

    @Test
    fun sidebar_preserves_order_settings_target_and_live_localization() {
        val english = mainNavItems(PharmStringsEn)
        val thai = mainNavItems(PharmStringsTh)
        assertEquals(17, english.size)
        assertEquals(k(Sell::class), english.first().route)
        assertEquals(english.map { it.route }, thai.map { it.route })
        assertNotEquals(english.first().label, thai.first().label)
        assertEquals(Settings, routeForKey(k(Settings::class)))
        assertTrue(english.first().pinned)
    }

    @Test
    fun unknown_routes_have_no_destination_or_sidebar_target() {
        assertNull(destinationFor(null))
        assertNull(destinationFor("unknown/argument"))
        assertNull(routeForKey("unknown"))
    }

    @Test
    fun sidebar_minimum_roles_follow_the_shared_role_policy() {
        val minRoles: Map<String, Role> = mainDestinations.mapNotNull { d -> d.sidebar?.let { d.route.simpleName.orEmpty() to it.minRole } }.toMap()
        val expected: Map<String, Role> = mapOf(
            "Sell" to Role.USER, "SalesHistory" to Role.USER, "Customers" to Role.USER,
            "Stock" to Role.USER, "Movements" to Role.USER, "OfflineSync" to Role.USER, "Help" to Role.USER,
            "StockCounts" to Role.MANAGER, "Expiry" to Role.MANAGER, "LabelPrint" to Role.MANAGER,
            "Imports" to Role.MANAGER, "Suppliers" to Role.MANAGER, "Reports" to Role.MANAGER,
            "Users" to Role.MANAGER,
            "Profit" to Role.ADMIN, "Ky9" to Role.ADMIN, "Settings" to Role.ADMIN,
        )
        assertEquals(expected, minRoles)
    }
}
