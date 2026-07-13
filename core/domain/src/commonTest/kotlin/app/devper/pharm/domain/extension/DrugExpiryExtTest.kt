package app.devper.pharm.domain.extension

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.Drug
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class DrugExpiryExtTest {

    private fun drug(expiry: LocalDate?) = Drug(
        id = "a",
        name = "Drug",
        genericName = null,
        type = null,
        strength = null,
        barcode = null,
        sellPrice = Money(10.0),
        costPrice = Money(5.0),
        stock = Quantity(10),
        minStock = Quantity.Zero,
        unit = null,
        regNo = null,
        nextLotExpiry = expiry,
    )

    private val today = LocalDate(2026, 7, 10)

    @Test
    fun no_next_lot_yields_null() {
        assertNull(drug(null).nextLotDaysLeft(today))
    }

    @Test
    fun future_expiry_counts_days_ahead() {
        assertEquals(30, drug(LocalDate(2026, 8, 9)).nextLotDaysLeft(today))
    }

    @Test
    fun today_expiry_is_zero() {
        assertEquals(0, drug(today).nextLotDaysLeft(today))
    }

    @Test
    fun past_expiry_is_negative() {
        assertEquals(-10, drug(LocalDate(2026, 6, 30)).nextLotDaysLeft(today))
    }
}
