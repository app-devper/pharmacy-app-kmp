package app.devper.pharm.ui.i18n

import kotlin.test.Test
import kotlin.test.assertTrue

class SearchPlaceholderTest {

    @Test
    fun english_search_placeholders_end_with_ellipsis() {
        assertSearchPlaceholders(PharmStringsEn)
    }

    @Test
    fun thai_search_placeholders_end_with_ellipsis() {
        assertSearchPlaceholders(PharmStringsTh)
    }

    @Test
    fun english_form_placeholders_end_with_ellipsis() {
        assertFormPlaceholders(PharmStringsEn)
    }

    @Test
    fun thai_form_placeholders_end_with_ellipsis() {
        assertFormPlaceholders(PharmStringsTh)
    }

    @Test
    fun loading_copy_ends_with_ellipsis() {
        assertTrue(PharmStringsEn.commonLoading.endsWith("…"))
        assertTrue(PharmStringsTh.commonLoading.endsWith("…"))
    }

    private fun assertSearchPlaceholders(strings: PharmStrings) {
        listOf(
            strings.customersSearchPlaceholder,
            strings.expirySearchPlaceholder,
            strings.importsSearchPlaceholder,
            strings.importsFormSupplierSearchPlaceholder,
            strings.importsFormPickDrugSearchPlaceholder,
            strings.kySearchPlaceholder,
            strings.labelsSearchPlaceholder,
            strings.movementsSearchPlaceholder,
            strings.planningLowStockSearchPlaceholder,
            strings.salesHistorySearchPlaceholder,
            strings.sellSearchPlaceholder,
            strings.sellCustomerSearchPlaceholder,
            strings.stockSearchPlaceholder,
            strings.stockCountHistorySearchPlaceholder,
            strings.stockCountFormSearchPlaceholder,
            strings.suppliersSearchPlaceholder,
            strings.usersSearchPlaceholder,
        ).forEach { placeholder ->
            assertTrue(placeholder.endsWith("…"), placeholder)
        }
    }

    private fun assertFormPlaceholders(strings: PharmStrings) {
        listOf(
            strings.customersFormNamePlaceholder,
            strings.importsFormDocNoPlaceholder,
            strings.importsFormSupplierPlaceholder,
            strings.importsFormPickDrugPlaceholder,
            strings.importsFormHeaderLotNumberPlaceholder,
            strings.importsHeaderInvoicePlaceholder,
            strings.kyHeaderStatusPlaceholder,
            strings.loginUsernamePlaceholder,
            strings.loginPasswordPlaceholder,
            strings.reportsDatePlaceholder,
            strings.salesHistoryReturnReasonPlaceholder,
            strings.settingsStoreNamePlaceholder,
            strings.settingsReceiptHeaderPlaceholder,
            strings.settingsReceiptFooterPlaceholder,
            strings.settingsStockLowThresholdPlaceholder,
            strings.stockUnitPlaceholder,
            strings.stockLotNumberPlaceholder,
            strings.stockCountFormNotePlaceholder,
            strings.suppliersTaxIdPlaceholder,
            strings.suppliersFormCompanyPlaceholder,
            strings.suppliersFormAddressPlaceholder,
            strings.suppliersFormNotesPlaceholder,
        ).forEach { placeholder ->
            assertTrue(placeholder.endsWith("…"), placeholder)
        }
    }
}
