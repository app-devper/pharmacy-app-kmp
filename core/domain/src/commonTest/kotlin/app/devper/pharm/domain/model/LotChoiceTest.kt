package app.devper.pharm.domain.model

import app.devper.pharm.common.value.Quantity
import kotlinx.datetime.LocalDate
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class LotChoiceTest {

    private fun lot(id: String, expiry: String?, writtenOff: Boolean = false) = DrugLot(
        id = id, drugId = "d1", lotNumber = id, expiryDate = expiry?.let(LocalDate::parse), importDate = null,
        quantity = Quantity(10), remaining = Quantity(10), writtenOff = writtenOff,
    )

    @Test
    fun an_increase_offers_lots_not_written_off_latest_expiry_first_and_picks_the_latest() {
        val choice = LotChoice.forIncrease(listOf(lot("early", "2026-12-31"), lot("late", "2027-06-30"), lot("gone", "2028-01-01", writtenOff = true)))!!
        assertEquals(listOf("late", "early"), choice.lots.map { it.id })
        assertEquals(LotTarget.Existing("late"), choice.target)
    }

    @Test
    fun a_drug_whose_lots_are_all_written_off_is_not_lot_tracked() {
        assertNull(LotChoice.forIncrease(emptyList()))
        assertNull(LotChoice.forIncrease(listOf(lot("gone", "2027-01-01", writtenOff = true))))
    }

    @Test
    fun a_new_lot_needs_a_number_and_a_real_expiry() {
        val choice = LotChoice.forIncrease(listOf(lot("a", "2026-12-31")))!!.choose(LotChoice.NEW_LOT)
        assertNull(choice.target)
        assertNull(choice.withNewLotNumber("N1").withNewLotExpiry("2027-02-30").target)
        assertNull(choice.withNewLotNumber("  ").withNewLotExpiry("2027-03-01").target)
        assertEquals(
            LotTarget.New("N1", LocalDate(2027, 3, 1)),
            choice.withNewLotNumber(" N1 ").withNewLotExpiry("2027-03-01").target,
        )
    }
}
