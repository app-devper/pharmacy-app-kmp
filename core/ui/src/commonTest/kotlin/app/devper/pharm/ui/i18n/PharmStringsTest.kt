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
        check("offlineSyncSubtitle", PharmStringsTh.offlineSyncSubtitle, PharmStringsEn.offlineSyncSubtitle, mismatches)
        check("offlineSyncRetryAllCta", PharmStringsTh.offlineSyncRetryAllCta, PharmStringsEn.offlineSyncRetryAllCta, mismatches)
        check("offlineSyncEmptyTitle", PharmStringsTh.offlineSyncEmptyTitle, PharmStringsEn.offlineSyncEmptyTitle, mismatches)
        check("offlineSyncMetricsTotal", PharmStringsTh.offlineSyncMetricsTotal, PharmStringsEn.offlineSyncMetricsTotal, mismatches)
        check("offlineSyncStatusPending", PharmStringsTh.offlineSyncStatusPending, PharmStringsEn.offlineSyncStatusPending, mismatches)
        check("offlineSyncStatusFailed", PharmStringsTh.offlineSyncStatusFailed, PharmStringsEn.offlineSyncStatusFailed, mismatches)
        check("offlineSyncRetryRowCta", PharmStringsTh.offlineSyncRetryRowCta, PharmStringsEn.offlineSyncRetryRowCta, mismatches)
        check("offlineSyncDeleteConfirmTitle", PharmStringsTh.offlineSyncDeleteConfirmTitle, PharmStringsEn.offlineSyncDeleteConfirmTitle, mismatches)
        check("expirySubtitle", PharmStringsTh.expirySubtitle, PharmStringsEn.expirySubtitle, mismatches)
        check("expirySelectAll", PharmStringsTh.expirySelectAll, PharmStringsEn.expirySelectAll, mismatches)
        check("expiryWriteoffCta", PharmStringsTh.expiryWriteoffCta, PharmStringsEn.expiryWriteoffCta, mismatches)
        check("expiryHeaderDrugName", PharmStringsTh.expiryHeaderDrugName, PharmStringsEn.expiryHeaderDrugName, mismatches)
        check("expiryHeaderLotNumber", PharmStringsTh.expiryHeaderLotNumber, PharmStringsEn.expiryHeaderLotNumber, mismatches)
        check("expiryStatusExpired", PharmStringsTh.expiryStatusExpired, PharmStringsEn.expiryStatusExpired, mismatches)
        check("expiryEmpty", PharmStringsTh.expiryEmpty, PharmStringsEn.expiryEmpty, mismatches)
        check("expiryConfirmTitle", PharmStringsTh.expiryConfirmTitle, PharmStringsEn.expiryConfirmTitle, mismatches)
        check("expiryResultSuccessTitle", PharmStringsTh.expiryResultSuccessTitle, PharmStringsEn.expiryResultSuccessTitle, mismatches)
        check("movementsSubtitle", PharmStringsTh.movementsSubtitle, PharmStringsEn.movementsSubtitle, mismatches)
        check("movementsEmpty", PharmStringsTh.movementsEmpty, PharmStringsEn.movementsEmpty, mismatches)
        check("movementsHeaderType", PharmStringsTh.movementsHeaderType, PharmStringsEn.movementsHeaderType, mismatches)
        check("movementsHeaderRef", PharmStringsTh.movementsHeaderRef, PharmStringsEn.movementsHeaderRef, mismatches)
        check("movementsNextPage", PharmStringsTh.movementsNextPage, PharmStringsEn.movementsNextPage, mismatches)
        check("labelsSubtitle", PharmStringsTh.labelsSubtitle, PharmStringsEn.labelsSubtitle, mismatches)
        check("labelsEmpty", PharmStringsTh.labelsEmpty, PharmStringsEn.labelsEmpty, mismatches)
        check("labelsClear", PharmStringsTh.labelsClear, PharmStringsEn.labelsClear, mismatches)
        check("labelsPrinting", PharmStringsTh.labelsPrinting, PharmStringsEn.labelsPrinting, mismatches)
        check("labelsPrintSuccess", PharmStringsTh.labelsPrintSuccess, PharmStringsEn.labelsPrintSuccess, mismatches)
        check("helpSubtitle", PharmStringsTh.helpSubtitle, PharmStringsEn.helpSubtitle, mismatches)
        check("helpToc", PharmStringsTh.helpToc, PharmStringsEn.helpToc, mismatches)
        check("helpNotFound", PharmStringsTh.helpNotFound, PharmStringsEn.helpNotFound, mismatches)
        check("helpTipFocusSearch", PharmStringsTh.helpTipFocusSearch, PharmStringsEn.helpTipFocusSearch, mismatches)
        check("planningTitle", PharmStringsTh.planningTitle, PharmStringsEn.planningTitle, mismatches)
        check("planningRefreshCta", PharmStringsTh.planningRefreshCta, PharmStringsEn.planningRefreshCta, mismatches)
        check("planningAddPoCta", PharmStringsTh.planningAddPoCta, PharmStringsEn.planningAddPoCta, mismatches)
        check("planningLowStockTitle", PharmStringsTh.planningLowStockTitle, PharmStringsEn.planningLowStockTitle, mismatches)
        check("planningReorderTitle", PharmStringsTh.planningReorderTitle, PharmStringsEn.planningReorderTitle, mismatches)
        check("planningHeaderMin", PharmStringsTh.planningHeaderMin, PharmStringsEn.planningHeaderMin, mismatches)
        check("planningHeaderRecommend", PharmStringsTh.planningHeaderRecommend, PharmStringsEn.planningHeaderRecommend, mismatches)
        check("bulkImportTitle", PharmStringsTh.bulkImportTitle, PharmStringsEn.bulkImportTitle, mismatches)
        check("bulkImportSubtitle", PharmStringsTh.bulkImportSubtitle, PharmStringsEn.bulkImportSubtitle, mismatches)
        check("bulkImportDownloadTemplate", PharmStringsTh.bulkImportDownloadTemplate, PharmStringsEn.bulkImportDownloadTemplate, mismatches)
        check("bulkImportValidateCta", PharmStringsTh.bulkImportValidateCta, PharmStringsEn.bulkImportValidateCta, mismatches)
        check("bulkImportImportAllCta", PharmStringsTh.bulkImportImportAllCta, PharmStringsEn.bulkImportImportAllCta, mismatches)
        check("bulkImportResultAllSuccess", PharmStringsTh.bulkImportResultAllSuccess, PharmStringsEn.bulkImportResultAllSuccess, mismatches)
        check("bulkImportResultPartial", PharmStringsTh.bulkImportResultPartial, PharmStringsEn.bulkImportResultPartial, mismatches)
        check("bulkImportStatusReady", PharmStringsTh.bulkImportStatusReady, PharmStringsEn.bulkImportStatusReady, mismatches)
        check("bulkImportStatusError", PharmStringsTh.bulkImportStatusError, PharmStringsEn.bulkImportStatusError, mismatches)
        check("stockCountHistoryTitle", PharmStringsTh.stockCountHistoryTitle, PharmStringsEn.stockCountHistoryTitle, mismatches)
        check("stockCountHistorySubtitle", PharmStringsTh.stockCountHistorySubtitle, PharmStringsEn.stockCountHistorySubtitle, mismatches)
        check("stockCountHistoryNewCta", PharmStringsTh.stockCountHistoryNewCta, PharmStringsEn.stockCountHistoryNewCta, mismatches)
        check("stockCountHistoryEmpty", PharmStringsTh.stockCountHistoryEmpty, PharmStringsEn.stockCountHistoryEmpty, mismatches)
        check("stockCountHeaderRound", PharmStringsTh.stockCountHeaderRound, PharmStringsEn.stockCountHeaderRound, mismatches)
        check("stockCountStatusAdjusted", PharmStringsTh.stockCountStatusAdjusted, PharmStringsEn.stockCountStatusAdjusted, mismatches)
        check("stockCountFormCounted", PharmStringsTh.stockCountFormCounted, PharmStringsEn.stockCountFormCounted, mismatches)
        check("stockCountFormFillSystem", PharmStringsTh.stockCountFormFillSystem, PharmStringsEn.stockCountFormFillSystem, mismatches)
        check("stockCountFormConfirmTitle", PharmStringsTh.stockCountFormConfirmTitle, PharmStringsEn.stockCountFormConfirmTitle, mismatches)
        check("stockCountFormConfirmMessage", PharmStringsTh.stockCountFormConfirmMessage, PharmStringsEn.stockCountFormConfirmMessage, mismatches)
        assertEquals(emptyList(), mismatches, "Found Thai/English entries that are identical (likely missing translation)")
    }

    @Test
    fun batch13_lambda_keys_interpolate_values_per_locale() {
        assertTrue(PharmStringsTh.stockCountFormUnitLabel("เม็ด").contains("เม็ด"))
        assertTrue(PharmStringsEn.stockCountFormUnitLabel("tablet").contains("tablet"))
        val thStatus = PharmStringsTh.stockCountFormStatusLine(10, 5, 3, 8)
        val enStatus = PharmStringsEn.stockCountFormStatusLine(10, 5, 3, 8)
        assertTrue(thStatus.contains("10") && thStatus.contains("8"))
        assertTrue(enStatus.contains("10") && enStatus.contains("8"))
        assertNotEquals(thStatus, enStatus)
        assertTrue(PharmStringsTh.stockCountFormSaveCountLabel(5).contains("5"))
        assertTrue(PharmStringsEn.stockCountFormSaveCountLabel(5).contains("5"))
        assertTrue(PharmStringsTh.stockCountFormTopDiscrepancy(3).contains("3"))
        assertTrue(PharmStringsEn.stockCountFormTopDiscrepancy(3).contains("3"))
    }

    @Test
    fun batch12_lambda_keys_interpolate_values_per_locale() {
        assertTrue(PharmStringsTh.planningDaysLeftLabel(5).contains("5"))
        assertTrue(PharmStringsEn.planningDaysLeftLabel(5).contains("5"))
        assertNotEquals(PharmStringsTh.planningDaysLeftLabel(5), PharmStringsEn.planningDaysLeftLabel(5))
        val thMeta = PharmStringsTh.planningMetaLine("2.5", "10 วัน")
        val enMeta = PharmStringsEn.planningMetaLine("2.5", "10 day(s)")
        assertTrue(thMeta.contains("2.5") && thMeta.contains("10"))
        assertTrue(enMeta.contains("2.5") && enMeta.contains("10"))
        assertTrue(PharmStringsTh.bulkImportValidatedReady(3).contains("3"))
        assertTrue(PharmStringsEn.bulkImportValidatedReady(3).contains("3"))
        assertTrue(PharmStringsTh.bulkImportResultTitle(7).contains("7"))
        assertTrue(PharmStringsEn.bulkImportResultTitle(7).contains("7"))
        val thRes = PharmStringsTh.bulkImportResultSummary(8, 10)
        val enRes = PharmStringsEn.bulkImportResultSummary(8, 10)
        assertTrue(thRes.contains("8") && thRes.contains("10"))
        assertTrue(enRes.contains("8") && enRes.contains("10"))
        assertNotEquals(thRes, enRes)
    }

    @Test
    fun batch11_lambda_keys_interpolate_values_per_locale() {
        val thPag = PharmStringsTh.movementsPagination(2, 5)
        val enPag = PharmStringsEn.movementsPagination(2, 5)
        assertTrue(thPag.contains("2") && thPag.contains("5"))
        assertTrue(enPag.contains("2") && enPag.contains("5"))
        assertNotEquals(thPag, enPag)
        assertTrue(PharmStringsTh.movementsShownOf(10, 25).contains("10"))
        assertTrue(PharmStringsEn.movementsShownOf(10, 25).contains("25"))
        assertTrue(PharmStringsTh.labelsListTitle(3).contains("3"))
        assertTrue(PharmStringsEn.labelsListTitle(3).contains("3"))
        assertTrue(PharmStringsTh.labelsPrintCount(7).contains("7"))
        assertTrue(PharmStringsEn.labelsPrintCount(7).contains("7"))
        assertTrue(PharmStringsTh.labelsPreviewLabel("50×30").contains("50×30"))
        assertTrue(PharmStringsEn.labelsPreviewLabel("50×30").contains("50×30"))
    }

    @Test
    fun expiry_lambda_keys_interpolate_values_per_locale() {
        assertTrue(PharmStringsTh.expiryStatusDaysLeft(7).contains("7"))
        assertTrue(PharmStringsEn.expiryStatusDaysLeft(7).contains("7"))
        assertNotEquals(PharmStringsTh.expiryStatusDaysLeft(7), PharmStringsEn.expiryStatusDaysLeft(7))
        assertTrue(PharmStringsTh.expiryWriteoffSelectedLabel(5).contains("5"))
        assertTrue(PharmStringsEn.expiryWriteoffSelectedLabel(5).contains("5"))
        assertTrue(PharmStringsTh.expiryConfirmMessage(3).contains("3"))
        assertTrue(PharmStringsEn.expiryConfirmMessage(3).contains("3"))
        val thSummary = PharmStringsTh.expiryResultSummary(2, 5)
        val enSummary = PharmStringsEn.expiryResultSummary(2, 5)
        assertTrue(thSummary.contains("2") && thSummary.contains("5"))
        assertTrue(enSummary.contains("2") && enSummary.contains("5"))
        assertNotEquals(thSummary, enSummary)
    }

    @Test
    fun offline_sync_attempts_label_interpolates_count_per_locale() {
        val th = PharmStringsTh.offlineSyncAttemptsLabel(3)
        val en = PharmStringsEn.offlineSyncAttemptsLabel(3)
        assertTrue(th.contains("3"))
        assertTrue(en.contains("3"))
        assertNotEquals(th, en)
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
