package app.devper.pharm.ui.designsystem

import app.devper.pharm.domain.extension.Tier
import app.devper.pharm.ui.i18n.PharmStringsTh
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PriceTierBadgeTest {

    @Test
    fun retailAndBlankTiersAreNotBadged() {
        assertFalse(showsPriceTierBadge(Tier.Retail))
        assertFalse(showsPriceTierBadge(""))
        assertFalse(showsPriceTierBadge("   "))
    }

    @Test
    fun everyOtherTierIsBadged() {
        assertTrue(showsPriceTierBadge(Tier.Wholesale))
        assertTrue(showsPriceTierBadge(Tier.Regular))
    }

    @Test
    fun knownTiersReadAsTheirLabel() {
        assertEquals(PharmStringsTh.sellTierWholesaleLabel, priceTierLabel(Tier.Wholesale, PharmStringsTh))
        assertEquals(PharmStringsTh.sellTierRegularLabel, priceTierLabel(Tier.Regular, PharmStringsTh))
    }

    @Test
    fun anUnknownTierFallsBackToItsOwnValue() {
        assertEquals("vip", priceTierLabel("vip", PharmStringsTh))
    }
}
