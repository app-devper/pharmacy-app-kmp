package app.devper.pharm.ui.i18n

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PharmStringsTablesTest {

    @Test
    fun th_and_en_tables_differ_on_every_localised_key() {
        val mismatches = mutableListOf<String>()
        check("commonCancel", PharmStringsTh.commonCancel, PharmStringsEn.commonCancel, mismatches)
        check("commonSave", PharmStringsTh.commonSave, PharmStringsEn.commonSave, mismatches)
        check("commonDelete", PharmStringsTh.commonDelete, PharmStringsEn.commonDelete, mismatches)
        check("commonEdit", PharmStringsTh.commonEdit, PharmStringsEn.commonEdit, mismatches)
        check("commonAdd", PharmStringsTh.commonAdd, PharmStringsEn.commonAdd, mismatches)
        check("commonSearch", PharmStringsTh.commonSearch, PharmStringsEn.commonSearch, mismatches)
        check("commonConfirm", PharmStringsTh.commonConfirm, PharmStringsEn.commonConfirm, mismatches)
        check("commonRefresh", PharmStringsTh.commonRefresh, PharmStringsEn.commonRefresh, mismatches)
        check("commonUnitPiece", PharmStringsTh.commonUnitPiece, PharmStringsEn.commonUnitPiece, mismatches)
        check("commonUnitTablet", PharmStringsTh.commonUnitTablet, PharmStringsEn.commonUnitTablet, mismatches)
        check("settingsLocaleTitle", PharmStringsTh.settingsLocaleTitle, PharmStringsEn.settingsLocaleTitle, mismatches)
        check("navSell", PharmStringsTh.navSell, PharmStringsEn.navSell, mismatches)
        check("navSalesHistory", PharmStringsTh.navSalesHistory, PharmStringsEn.navSalesHistory, mismatches)
        check("navStock", PharmStringsTh.navStock, PharmStringsEn.navStock, mismatches)
        check("navCustomers", PharmStringsTh.navCustomers, PharmStringsEn.navCustomers, mismatches)
        check("navReports", PharmStringsTh.navReports, PharmStringsEn.navReports, mismatches)
        check("navSettings", PharmStringsTh.navSettings, PharmStringsEn.navSettings, mismatches)
        check("titleSell", PharmStringsTh.titleSell, PharmStringsEn.titleSell, mismatches)
        check("profileTitle", PharmStringsTh.profileTitle, PharmStringsEn.profileTitle, mismatches)
        check("profileSectionPersonal", PharmStringsTh.profileSectionPersonal, PharmStringsEn.profileSectionPersonal, mismatches)
        check("profileFirstName", PharmStringsTh.profileFirstName, PharmStringsEn.profileFirstName, mismatches)
        check("profileLastName", PharmStringsTh.profileLastName, PharmStringsEn.profileLastName, mismatches)
        check("profileEmail", PharmStringsTh.profileEmail, PharmStringsEn.profileEmail, mismatches)
        check("profilePasswordOld", PharmStringsTh.profilePasswordOld, PharmStringsEn.profilePasswordOld, mismatches)
        check("profilePasswordChanged", PharmStringsTh.profilePasswordChanged, PharmStringsEn.profilePasswordChanged, mismatches)
        check("profileDisplayTheme", PharmStringsTh.profileDisplayTheme, PharmStringsEn.profileDisplayTheme, mismatches)
        check("profileThemeLight", PharmStringsTh.profileThemeLight, PharmStringsEn.profileThemeLight, mismatches)
        check("profileFontMd", PharmStringsTh.profileFontMd, PharmStringsEn.profileFontMd, mismatches)
        check("profileDensityComfortable", PharmStringsTh.profileDensityComfortable, PharmStringsEn.profileDensityComfortable, mismatches)
        check("settingsToolbarSubtitle", PharmStringsTh.settingsToolbarSubtitle, PharmStringsEn.settingsToolbarSubtitle, mismatches)
        check("settingsTabStore", PharmStringsTh.settingsTabStore, PharmStringsEn.settingsTabStore, mismatches)
        check("settingsTabReceipt", PharmStringsTh.settingsTabReceipt, PharmStringsEn.settingsTabReceipt, mismatches)
        check("settingsTabPharmacist", PharmStringsTh.settingsTabPharmacist, PharmStringsEn.settingsTabPharmacist, mismatches)
        check("settingsStoreNameLabel", PharmStringsTh.settingsStoreNameLabel, PharmStringsEn.settingsStoreNameLabel, mismatches)
        check("settingsStoreAddress", PharmStringsTh.settingsStoreAddress, PharmStringsEn.settingsStoreAddress, mismatches)
        check("settingsStoreTaxId", PharmStringsTh.settingsStoreTaxId, PharmStringsEn.settingsStoreTaxId, mismatches)
        check("settingsReceiptHeader", PharmStringsTh.settingsReceiptHeader, PharmStringsEn.settingsReceiptHeader, mismatches)
        check("settingsReceiptPaperWidth", PharmStringsTh.settingsReceiptPaperWidth, PharmStringsEn.settingsReceiptPaperWidth, mismatches)
        check("settingsStockLowThresholdLabel", PharmStringsTh.settingsStockLowThresholdLabel, PharmStringsEn.settingsStockLowThresholdLabel, mismatches)
        check("settingsStockReorderDays", PharmStringsTh.settingsStockReorderDays, PharmStringsEn.settingsStockReorderDays, mismatches)
        check("settingsPharmacistName", PharmStringsTh.settingsPharmacistName, PharmStringsEn.settingsPharmacistName, mismatches)
        check("settingsKySkipAuto", PharmStringsTh.settingsKySkipAuto, PharmStringsEn.settingsKySkipAuto, mismatches)
        check("settingsKyDefaultBuyerAddress", PharmStringsTh.settingsKyDefaultBuyerAddress, PharmStringsEn.settingsKyDefaultBuyerAddress, mismatches)
        check("customersListSubtitle", PharmStringsTh.customersListSubtitle, PharmStringsEn.customersListSubtitle, mismatches)
        check("customersSearchPlaceholder", PharmStringsTh.customersSearchPlaceholder, PharmStringsEn.customersSearchPlaceholder, mismatches)
        check("customersAddCta", PharmStringsTh.customersAddCta, PharmStringsEn.customersAddCta, mismatches)
        check("customersHeaderTotalSpent", PharmStringsTh.customersHeaderTotalSpent, PharmStringsEn.customersHeaderTotalSpent, mismatches)
        check("customersListEmpty", PharmStringsTh.customersListEmpty, PharmStringsEn.customersListEmpty, mismatches)
        check("customersDetailNoPhone", PharmStringsTh.customersDetailNoPhone, PharmStringsEn.customersDetailNoPhone, mismatches)
        check("customersAllergyLabel", PharmStringsTh.customersAllergyLabel, PharmStringsEn.customersAllergyLabel, mismatches)
        check("customersBadgeVoided", PharmStringsTh.customersBadgeVoided, PharmStringsEn.customersBadgeVoided, mismatches)
        check("customersFormEditTitle", PharmStringsTh.customersFormEditTitle, PharmStringsEn.customersFormEditTitle, mismatches)
        check("customersFormFullName", PharmStringsTh.customersFormFullName, PharmStringsEn.customersFormFullName, mismatches)
        check("customersTierLabel", PharmStringsTh.customersTierLabel, PharmStringsEn.customersTierLabel, mismatches)
        check("customersTierWholesale", PharmStringsTh.customersTierWholesale, PharmStringsEn.customersTierWholesale, mismatches)
        check("suppliersListSubtitle", PharmStringsTh.suppliersListSubtitle, PharmStringsEn.suppliersListSubtitle, mismatches)
        check("suppliersAddCta", PharmStringsTh.suppliersAddCta, PharmStringsEn.suppliersAddCta, mismatches)
        check("suppliersListEmpty", PharmStringsTh.suppliersListEmpty, PharmStringsEn.suppliersListEmpty, mismatches)
        check("suppliersHeaderName", PharmStringsTh.suppliersHeaderName, PharmStringsEn.suppliersHeaderName, mismatches)
        check("suppliersHeaderContact", PharmStringsTh.suppliersHeaderContact, PharmStringsEn.suppliersHeaderContact, mismatches)
        check("suppliersHeaderDetails", PharmStringsTh.suppliersHeaderDetails, PharmStringsEn.suppliersHeaderDetails, mismatches)
        check("suppliersDeleteConfirmTitle", PharmStringsTh.suppliersDeleteConfirmTitle, PharmStringsEn.suppliersDeleteConfirmTitle, mismatches)
        check("suppliersFormAddTitle", PharmStringsTh.suppliersFormAddTitle, PharmStringsEn.suppliersFormAddTitle, mismatches)
        check("suppliersFormEditTitle", PharmStringsTh.suppliersFormEditTitle, PharmStringsEn.suppliersFormEditTitle, mismatches)
        check("suppliersFormCompanyName", PharmStringsTh.suppliersFormCompanyName, PharmStringsEn.suppliersFormCompanyName, mismatches)
        check("suppliersFormTaxId", PharmStringsTh.suppliersFormTaxId, PharmStringsEn.suppliersFormTaxId, mismatches)
        check("usersListSubtitle", PharmStringsTh.usersListSubtitle, PharmStringsEn.usersListSubtitle, mismatches)
        check("usersAddCta", PharmStringsTh.usersAddCta, PharmStringsEn.usersAddCta, mismatches)
        check("usersListEmpty", PharmStringsTh.usersListEmpty, PharmStringsEn.usersListEmpty, mismatches)
        check("usersOwnAccountBadge", PharmStringsTh.usersOwnAccountBadge, PharmStringsEn.usersOwnAccountBadge, mismatches)
        check("usersStatusActive", PharmStringsTh.usersStatusActive, PharmStringsEn.usersStatusActive, mismatches)
        check("usersActionChangeRole", PharmStringsTh.usersActionChangeRole, PharmStringsEn.usersActionChangeRole, mismatches)
        check("usersFormAddTitle", PharmStringsTh.usersFormAddTitle, PharmStringsEn.usersFormAddTitle, mismatches)
        check("usersFormUsername", PharmStringsTh.usersFormUsername, PharmStringsEn.usersFormUsername, mismatches)
        check("usersFormPasswordHint", PharmStringsTh.usersFormPasswordHint, PharmStringsEn.usersFormPasswordHint, mismatches)
        check("usersConfirmDeleteTitle", PharmStringsTh.usersConfirmDeleteTitle, PharmStringsEn.usersConfirmDeleteTitle, mismatches)
        check("salesHistorySubtitle", PharmStringsTh.salesHistorySubtitle, PharmStringsEn.salesHistorySubtitle, mismatches)
        check("salesHistorySearchPlaceholder", PharmStringsTh.salesHistorySearchPlaceholder, PharmStringsEn.salesHistorySearchPlaceholder, mismatches)
        check("salesHistoryEmptySearching", PharmStringsTh.salesHistoryEmptySearching, PharmStringsEn.salesHistoryEmptySearching, mismatches)
        check("salesHistoryHeaderTime", PharmStringsTh.salesHistoryHeaderTime, PharmStringsEn.salesHistoryHeaderTime, mismatches)
        check("salesHistoryHeaderBillNo", PharmStringsTh.salesHistoryHeaderBillNo, PharmStringsEn.salesHistoryHeaderBillNo, mismatches)
        check("salesHistoryHeaderNet", PharmStringsTh.salesHistoryHeaderNet, PharmStringsEn.salesHistoryHeaderNet, mismatches)
        check("salesHistoryStatusOk", PharmStringsTh.salesHistoryStatusOk, PharmStringsEn.salesHistoryStatusOk, mismatches)
        check("salesHistoryWalkInCustomer", PharmStringsTh.salesHistoryWalkInCustomer, PharmStringsEn.salesHistoryWalkInCustomer, mismatches)
        check("salesHistoryActionViewBill", PharmStringsTh.salesHistoryActionViewBill, PharmStringsEn.salesHistoryActionViewBill, mismatches)
        check("salesHistoryActionReturn", PharmStringsTh.salesHistoryActionReturn, PharmStringsEn.salesHistoryActionReturn, mismatches)
        assertEquals(emptyList(), mismatches, "Found Thai/English entries that are identical (likely missing translation)")
    }

    @Test
    fun users_lambda_keys_interpolate_username_and_differ_per_locale() {
        val sampleName = "somchai"
        assertTrue(PharmStringsTh.usersConfirmDeleteMessage(sampleName).contains(sampleName))
        assertTrue(PharmStringsEn.usersConfirmDeleteMessage(sampleName).contains(sampleName))
        assertNotEquals(
            PharmStringsTh.usersConfirmDeleteMessage(sampleName),
            PharmStringsEn.usersConfirmDeleteMessage(sampleName),
        )
        assertTrue(PharmStringsTh.usersSetPasswordTitle("Somchai").contains("Somchai"))
        assertTrue(PharmStringsEn.usersSetPasswordTitle("Somchai").contains("Somchai"))
        assertTrue(PharmStringsTh.usersConfirmEnableMessage(sampleName).contains(sampleName))
        assertTrue(PharmStringsEn.usersConfirmSuspendMessage(sampleName).contains(sampleName))
    }

    @Test
    fun delete_confirm_message_lambda_interpolates_supplier_name_in_both_locales() {
        val th = PharmStringsTh.suppliersDeleteConfirmMessage("ACME Pharma")
        val en = PharmStringsEn.suppliersDeleteConfirmMessage("ACME Pharma")
        assertTrue(th.contains("ACME Pharma"), "Thai message should embed the supplier name: $th")
        assertTrue(en.contains("ACME Pharma"), "English message should embed the supplier name: $en")
        assertNotEquals(th, en, "Thai/English delete-confirm messages should differ")
    }

    @Test
    fun th_and_en_share_locale_name_strings_unchanged() {
        assertEquals("ไทย", PharmStringsTh.settingsLocaleTh)
        assertEquals("ไทย", PharmStringsEn.settingsLocaleTh)
        assertEquals("English", PharmStringsTh.settingsLocaleEn)
        assertEquals("English", PharmStringsEn.settingsLocaleEn)
    }

    @Test
    fun thai_table_uses_thai_save_label() {
        assertEquals("บันทึก", PharmStringsTh.commonSave)
        assertEquals("ยกเลิก", PharmStringsTh.commonCancel)
    }

    @Test
    fun english_table_uses_english_save_label() {
        assertEquals("Save", PharmStringsEn.commonSave)
        assertEquals("Cancel", PharmStringsEn.commonCancel)
    }

    private fun check(name: String, th: String, en: String, mismatches: MutableList<String>) {
        if (th == en) mismatches += "$name (\"$th\")"
        assertNotEquals(th, en, "Translation missing for $name: both tables share \"$th\"")
    }
}
