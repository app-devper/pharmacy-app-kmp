package app.devper.pharm.presentation.sell.components

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class CartLineRowTest {

    @Test
    fun zero_requests_removal_instead_of_updating_quantity() {
        assertIs<CartQtyEditResult.Remove>(resolveCartQtyEdit("0", currentQty = 3))
    }

    @Test
    fun quantity_is_clamped_to_supported_maximum() {
        val result = assertIs<CartQtyEditResult.Quantity>(resolveCartQtyEdit("10000", currentQty = 3))
        assertEquals(9999, result.value)
    }

    @Test
    fun invalid_input_keeps_current_quantity() {
        val result = assertIs<CartQtyEditResult.Quantity>(resolveCartQtyEdit("", currentQty = 3))
        assertEquals(3, result.value)
    }
}
