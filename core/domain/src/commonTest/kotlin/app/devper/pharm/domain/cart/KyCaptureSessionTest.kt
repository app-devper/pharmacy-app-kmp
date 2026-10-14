package app.devper.pharm.domain.cart

import app.devper.pharm.common.value.Money
import app.devper.pharm.common.value.Quantity
import app.devper.pharm.domain.model.CartLine
import app.devper.pharm.domain.model.Drug
import app.devper.pharm.domain.model.KyCaptureFields
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNull
import kotlin.test.assertTrue

class KyCaptureSessionTest {

    private fun line(id: String, vararg reportTypes: String, qty: Int = 1) = CartLine(
        drug = Drug(
            id = id, name = id, genericName = null, type = null, strength = null, barcode = null,
            sellPrice = Money(10.0), costPrice = Money(0.0), stock = Quantity(100), minStock = Quantity(0),
            unit = "เม็ด", regNo = null, reportTypes = reportTypes.toList(),
        ),
        qty = qty,
    )

    private val plain = listOf(line("para"))
    private val dangerous = listOf(line("tramadol", "ky11"))
    private val fields = KyCaptureFields(ky11BuyerName = "นาย ก", ky11Purpose = "ปวด")

    @Test
    fun a_cart_without_ky_drugs_proceeds_with_nothing_to_send() {
        val session = KyCaptureSession()
        assertEquals(KyCaptureSession.Decision.Proceed, session.decide(plain, skipAuto = false))
        assertEquals(KyCaptureSession.Submission(capture = null, skippedByCashier = false), session.submission())
    }

    @Test
    fun a_ky_cart_stops_for_capture_then_sends_what_was_captured() {
        val session = KyCaptureSession()
        val decision = assertIs<KyCaptureSession.Decision.NeedsCapture>(session.decide(dangerous, skipAuto = false))
        assertTrue(decision.required.needsKy11)
        session.capture(fields)
        val sent = session.submission()
        assertEquals(fields, sent.capture?.ky11)
        assertNull(sent.capture?.ky10)
        assertFalse(sent.skippedByCashier)
    }

    @Test
    fun a_precapture_lets_checkout_proceed_until_the_cart_changes() {
        val session = KyCaptureSession()
        assertTrue(session.precaptureNeeded(dangerous) != null)
        session.precapture(fields, dangerous)
        assertTrue(session.isPrecaptured)
        assertFalse(session.cartChanged(dangerous))
        assertEquals(KyCaptureSession.Decision.Proceed, session.decide(dangerous, skipAuto = false))

        val more = dangerous.map { it.copy(qty = 2) }
        assertTrue(session.cartChanged(more))
        assertFalse(session.isPrecaptured)
        assertIs<KyCaptureSession.Decision.NeedsCapture>(session.decide(more, skipAuto = false))
    }

    @Test
    fun a_cashier_skip_is_sent_without_a_capture() {
        val session = KyCaptureSession()
        session.precapture(fields, dangerous)
        session.decide(dangerous, skipAuto = false)
        session.skip()
        assertEquals(KyCaptureSession.Submission(capture = null, skippedByCashier = true), session.submission())
        assertFalse(session.isPrecaptured)
    }

    @Test
    fun the_shop_setting_to_skip_ky_proceeds_and_clears_any_capture() {
        val session = KyCaptureSession()
        session.capture(fields)
        assertEquals(KyCaptureSession.Decision.Proceed, session.decide(dangerous, skipAuto = true))
        assertEquals(KyCaptureSession.Submission(capture = null, skippedByCashier = false), session.submission())
    }

    @Test
    fun cancelling_forgets_the_capture_and_the_precapture() {
        val session = KyCaptureSession()
        session.precapture(fields, dangerous)
        session.cancel()
        assertFalse(session.isPrecaptured)
        assertIs<KyCaptureSession.Decision.NeedsCapture>(session.decide(dangerous, skipAuto = false))
    }

    @Test
    fun settling_after_a_sale_keeps_nothing_for_the_next_one() {
        val session = KyCaptureSession()
        session.decide(dangerous, skipAuto = false)
        session.capture(fields)
        session.settle()
        assertEquals(KyCaptureSession.Submission(capture = null, skippedByCashier = false), session.submission())
    }
}
