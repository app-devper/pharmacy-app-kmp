package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.model.KyRequired
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckoutUiStateTest {

    @Test
    fun canCheckout_true_when_cart_non_empty_and_tender_ok_with_no_modals() {
        val s = CheckoutUiState(cartIsEmpty = false, tenderOk = true)
        assertTrue(s.canCheckout)
    }

    @Test
    fun canCheckout_false_while_ky_capture_pending() {
        val s = CheckoutUiState(
            cartIsEmpty = false,
            tenderOk = true,
            kyCapturePending = KyRequired(),
        )
        assertFalse(s.canCheckout)
    }

    @Test
    fun canCheckout_false_while_oversell_pending() {
        val s = CheckoutUiState(
            cartIsEmpty = false,
            tenderOk = true,
            oversellPending = emptyList(),
        )
        assertFalse(s.canCheckout)
    }

    @Test
    fun canCheckout_false_while_checking_out() {
        val s = CheckoutUiState(
            cartIsEmpty = false,
            tenderOk = true,
            checkingOut = true,
        )
        assertFalse(s.canCheckout)
    }
}
