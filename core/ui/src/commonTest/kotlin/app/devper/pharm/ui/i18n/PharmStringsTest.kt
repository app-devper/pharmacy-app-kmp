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
        assertEquals(emptyList(), mismatches, "Found Thai/English entries that are identical (likely missing translation)")
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
