package app.devper.pharm.presentation.sell.flow

import app.devper.pharm.domain.model.KyRequired
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckoutUiStateTest {

    @Test
    fun canCheckout_true_when_cart_non_empty_with_no_modals() {
        val s = CheckoutUiState(cartIsEmpty = false)
        assertTrue(s.canCheckout)
    }

    @Test
    fun canCheckout_unaffected_by_paymentOpen() {
        val s = CheckoutUiState(cartIsEmpty = false, paymentOpen = true)
        assertTrue(s.canCheckout)
    }

    @Test
    fun canCheckout_false_while_ky_capture_pending() {
        val s = CheckoutUiState(
            cartIsEmpty = false,
            kyCapturePending = KyRequired(),
        )
        assertFalse(s.canCheckout)
    }

    @Test
    fun canCheckout_false_while_oversell_pending() {
        val s = CheckoutUiState(
            cartIsEmpty = false,
            oversellPending = emptyList(),
        )
        assertFalse(s.canCheckout)
    }

    @Test
    fun canCheckout_false_while_checking_out() {
        val s = CheckoutUiState(
            cartIsEmpty = false,
            checkingOut = true,
        )
        assertFalse(s.canCheckout)
    }
}
