package app.devper.pharm.presentation.sell

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class SellEscapeActionTest {

    private fun resolve(
        shortcutsVisible: Boolean = false,
        hasError: Boolean = false,
        overwriteSlotPending: Boolean = false,
        clearConfirmVisible: Boolean = false,
        skipKyConfirmVisible: Boolean = false,
        oversellPending: Boolean = false,
        kyCapturePending: Boolean = false,
        lineDiscountOpen: Boolean = false,
        cartDiscountOpen: Boolean = false,
        altUnitPickerOpen: Boolean = false,
        voidSheetOpen: Boolean = false,
        customerOpen: Boolean = false,
        parkedSheetOpen: Boolean = false,
        receiptVisible: Boolean = false,
    ) = resolveSellEscapeAction(
        shortcutsVisible, hasError, overwriteSlotPending,
        clearConfirmVisible, skipKyConfirmVisible, oversellPending, kyCapturePending,
        lineDiscountOpen, cartDiscountOpen, altUnitPickerOpen, voidSheetOpen,
        customerOpen, parkedSheetOpen, receiptVisible,
    )

    @Test
    fun returns_null_when_nothing_is_open() {
        assertNull(resolve())
    }

    @Test
    fun each_flag_resolves_to_its_action_when_no_higher_priority_flag_is_set() {
        assertEquals(SellEscapeAction.HideShortcuts, resolve(shortcutsVisible = true))
        assertEquals(SellEscapeAction.DismissErrors, resolve(hasError = true))
        assertEquals(SellEscapeAction.CancelOverwrite, resolve(overwriteSlotPending = true))
        assertEquals(SellEscapeAction.CancelClearCart, resolve(clearConfirmVisible = true))
        assertEquals(SellEscapeAction.CancelSkipKy, resolve(skipKyConfirmVisible = true))
        assertEquals(SellEscapeAction.DismissOversell, resolve(oversellPending = true))
        assertEquals(SellEscapeAction.DismissKyCapture, resolve(kyCapturePending = true))
        assertEquals(SellEscapeAction.CloseLineDiscount, resolve(lineDiscountOpen = true))
        assertEquals(SellEscapeAction.CloseCartDiscount, resolve(cartDiscountOpen = true))
        assertEquals(SellEscapeAction.CloseAltUnitPicker, resolve(altUnitPickerOpen = true))
        assertEquals(SellEscapeAction.CloseVoidSheet, resolve(voidSheetOpen = true))
        assertEquals(SellEscapeAction.CloseCustomer, resolve(customerOpen = true))
        assertEquals(SellEscapeAction.CloseParkedSheet, resolve(parkedSheetOpen = true))
        assertEquals(SellEscapeAction.DismissReceipt, resolve(receiptVisible = true))
    }

    @Test
    fun shortcuts_overlay_wins_over_everything_else() {
        assertEquals(
            SellEscapeAction.HideShortcuts,
            resolve(shortcutsVisible = true, hasError = true, receiptVisible = true, parkedSheetOpen = true),
        )
    }

    @Test
    fun error_wins_over_every_sheet_and_confirm() {
        assertEquals(
            SellEscapeAction.DismissErrors,
            resolve(
                hasError = true,
                overwriteSlotPending = true,
                clearConfirmVisible = true,
                voidSheetOpen = true,
                receiptVisible = true,
            ),
        )
    }

    @Test
    fun slot_overwrite_outranks_confirms_and_sheets() {
        assertEquals(
            SellEscapeAction.CancelOverwrite,
            resolve(overwriteSlotPending = true, clearConfirmVisible = true, parkedSheetOpen = true),
        )
    }

    @Test
    fun receipt_is_the_lowest_priority_dismiss() {
        assertEquals(SellEscapeAction.DismissReceipt, resolve(receiptVisible = true))
        assertEquals(
            SellEscapeAction.CloseParkedSheet,
            resolve(parkedSheetOpen = true, receiptVisible = true),
        )
    }

    @Test
    fun every_flag_set_resolves_to_the_highest_priority_action() {
        assertEquals(
            SellEscapeAction.HideShortcuts,
            resolve(
                shortcutsVisible = true, hasError = true, overwriteSlotPending = true,
                clearConfirmVisible = true, skipKyConfirmVisible = true,
                oversellPending = true, kyCapturePending = true, lineDiscountOpen = true,
                cartDiscountOpen = true, altUnitPickerOpen = true, voidSheetOpen = true,
                customerOpen = true, parkedSheetOpen = true, receiptVisible = true,
            ),
        )
    }

    @Test
    fun checkout_confirms_outrank_discount_and_picker_sheets() {
        assertEquals(
            SellEscapeAction.CancelSkipKy,
            resolve(skipKyConfirmVisible = true, lineDiscountOpen = true, altUnitPickerOpen = true),
        )
        assertEquals(
            SellEscapeAction.DismissOversell,
            resolve(oversellPending = true, cartDiscountOpen = true, customerOpen = true),
        )
        assertEquals(
            SellEscapeAction.DismissKyCapture,
            resolve(kyCapturePending = true, altUnitPickerOpen = true, voidSheetOpen = true),
        )
    }
}
