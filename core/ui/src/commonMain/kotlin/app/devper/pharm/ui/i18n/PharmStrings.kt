package app.devper.pharm.ui.i18n

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

interface PharmStrings {
    val commonCancel: String
    val commonSave: String
    val commonDelete: String
    val commonEdit: String
    val commonAdd: String
    val commonSearch: String
    val commonConfirm: String
    val commonClose: String
    val commonBack: String
    val commonLoading: String
    val commonRetry: String
    val commonMore: String
    val commonTotal: String
    val commonQty: String
    val commonUnit: String
    val commonPrice: String
    val commonStatus: String
    val commonNote: String
    val commonName: String
    val commonPhone: String
    val commonDate: String
    val commonFilter: String
    val commonExport: String
    val commonPrint: String
    val commonRefresh: String
    val commonUnitPiece: String
    val commonUnitTablet: String
    val commonUnitCapsule: String
    val commonRequired: String
    val commonOptional: String
    val commonYes: String
    val commonNo: String
    val settingsLocaleTitle: String
    val settingsLocaleTh: String
    val settingsLocaleEn: String
    val settingsLocaleAppliedInline: String
    val settingsLocaleRestartHint: String
    val loginUsernameLabel: String
    val loginUsernamePlaceholder: String
    val loginPasswordLabel: String
    val loginPasswordPlaceholder: String
    val loginSubmit: String
    val loginSubmitting: String
    val loginBrandName: String
    val loginBrandTagline: String
    val loginVersionPrefix: String
    val navSell: String
    val navSalesHistory: String
    val navStock: String
    val navStockCounts: String
    val navExpiry: String
    val navLabelPrint: String
    val navMovements: String
    val navOfflineSync: String
    val navImports: String
    val navSuppliers: String
    val navCustomers: String
    val navReports: String
    val navProfit: String
    val navKyForms: String
    val navUsers: String
    val navSettings: String
    val navHelp: String
    val titleSell: String
    val profileTitle: String
    val profileSectionPersonal: String
    val profileSectionPersonalSubtitle: String
    val profileSectionPassword: String
    val profileSectionPasswordSubtitle: String
    val profileSectionDisplay: String
    val profileSectionDisplaySubtitle: String
    val profileFirstName: String
    val profileLastName: String
    val profileEmail: String
    val profileSavedInline: String
    val profileSaving: String
    val profilePasswordIntro: String
    val profilePasswordOld: String
    val profilePasswordNew: String
    val profilePasswordConfirm: String
    val profilePasswordMismatch: String
    val profilePasswordChange: String
    val profilePasswordChanging: String
    val profilePasswordChanged: String
    val profilePasswordChangeFailed: String
    val profileDisplayTheme: String
    val profileDisplayFontSize: String
    val profileDisplayDensity: String
    val profileThemeLight: String
    val profileThemeDark: String
    val profileThemeAuto: String
    val profileFontSm: String
    val profileFontMd: String
    val profileFontLg: String
    val profileFontXl: String
    val profileDensityComfortable: String
    val profileDensityCompact: String
    val settingsToolbarSubtitle: String
    val settingsDirtySubtitle: String
    val settingsTabStore: String
    val settingsTabReceipt: String
    val settingsTabStock: String
    val settingsTabPharmacist: String
    val settingsTabKy: String
    val settingsStoreNameLabel: String
    val settingsStoreNamePlaceholder: String
    val settingsStoreAddress: String
    val settingsStoreTaxId: String
    val settingsStoreTimezone: String
    val settingsReceiptHeader: String
    val settingsReceiptHeaderPlaceholder: String
    val settingsReceiptFooter: String
    val settingsReceiptFooterPlaceholder: String
    val settingsReceiptFooterHint: String
    val settingsReceiptPaperWidth: String
    val settingsReceiptShowPharmacist: String
    val settingsStockLowThresholdLabel: String
    val settingsStockLowThresholdPlaceholder: String
    val settingsStockReorderDays: String
    val settingsStockReorderLookahead: String
    val settingsStockExpiringDays: String
    val settingsPharmacistName: String
    val settingsPharmacistLicenseNo: String
    val settingsKySkipAuto: String
    val settingsKySkipAutoHint: String
    val settingsKyDefaultBuyerAddress: String
    val settingsKyDefaultBuyerAddressHint: String
    val customersListSubtitle: String
    val customersSearchPlaceholder: String
    val customersAddCta: String
    val customersCountNoun: String
    val customersHeaderAllergyShort: String
    val customersHeaderTotalSpent: String
    val customersHeaderLastVisit: String
    val customersHeaderActions: String
    val customersListNotFound: String
    val customersListEmpty: String
    val customersActionHistory: String
    val customersDetailNoPhone: String
    val customersAllergyLabel: String
    val customersDetailNoSales: String
    val customersBadgeVoided: String
    val customersFormEditTitle: String
    val customersFormInfoSection: String
    val customersFormFullName: String
    val customersFormNamePlaceholder: String
    val customersFormAllergyHint: String
    val customersTierLabel: String
    val customersTierHint: String
    val customersTierRetail: String
    val customersTierRegular: String
    val customersTierWholesale: String
    val suppliersListSubtitle: String
    val suppliersSearchPlaceholder: String
    val suppliersAddCta: String
    val suppliersListNotFound: String
    val suppliersListEmpty: String
    val suppliersHeaderName: String
    val suppliersHeaderContact: String
    val suppliersHeaderTaxId: String
    val suppliersHeaderDetails: String
    val suppliersDeleteConfirmTitle: String
    val suppliersDeleteConfirmMessage: (String) -> String
    val suppliersFormAddTitle: String
    val suppliersFormEditTitle: String
    val suppliersFormInfoSection: String
    val suppliersFormCompanyName: String
    val suppliersFormCompanyPlaceholder: String
    val suppliersFormContactName: String
    val suppliersFormAddress: String
    val suppliersFormAddressPlaceholder: String
    val suppliersFormTaxId: String
    val suppliersFormNotesPlaceholder: String
    val usersListSubtitle: String
    val usersSearchPlaceholder: String
    val usersAddCta: String
    val usersAddFirstCta: String
    val usersCountNoun: String
    val usersOwnAccountBadge: String
    val usersListEmpty: String
    val usersListNotFound: String
    val usersCannotEdit: String
    val usersHeaderName: String
    val usersStatusActive: String
    val usersStatusSuspended: String
    val usersActionChangeRole: String
    val usersActionSetPassword: String
    val usersActionSuspend: String
    val usersActionEnable: String
    val usersConfirmDeleteTitle: String
    val usersConfirmDeleteMessage: (String) -> String
    val usersConfirmRoleTitle: String
    val usersConfirmEnableTitle: String
    val usersConfirmSuspendTitle: String
    val usersConfirmEnableMessage: (String) -> String
    val usersConfirmSuspendMessage: (String) -> String
    val usersSetPasswordTitle: (String) -> String
    val usersFormAddTitle: String
    val usersFormEditTitle: String
    val usersFormInfoSection: String
    val usersFormUsername: String
    val usersFormPasswordCreate: String
    val usersFormPasswordNew: String
    val usersFormPasswordHint: String
    val salesHistorySubtitle: String
    val salesHistorySearchPlaceholder: String
    val salesHistoryCountNoun: String
    val salesHistoryEmptySearching: String
    val salesHistoryEmptyDateRange: String
    val salesHistoryHeaderTime: String
    val salesHistoryHeaderBillNo: String
    val salesHistoryHeaderNet: String
    val salesHistoryStatsTotal: String
    val salesHistoryWalkInCustomer: String
    val salesHistoryStatusOk: String
    val salesHistoryActionViewBill: String
    val salesHistoryActionReturn: String
    val offlineSyncSubtitle: String
    val offlineSyncRetryAllCta: String
    val offlineSyncEmptyTitle: String
    val offlineSyncEmpty: String
    val offlineSyncMetricsTotal: String
    val offlineSyncMetricsLocation: String
    val offlineSyncMetricsAttempts: String
    val offlineSyncMetricsAttemptsSuffix: String
    val offlineSyncMetricsFailed: String
    val offlineSyncStatusFailed: String
    val offlineSyncStatusPending: String
    val offlineSyncStatusRetry: String
    val offlineSyncAttemptsLabel: (Int) -> String
    val offlineSyncRetryRowCta: String
    val offlineSyncDeleteConfirmTitle: String
    val offlineSyncDeleteConfirmMessage: String
    val expirySubtitle: String
    val expirySelectAll: String
    val expirySelectPartial: String
    val expiryWriteoffCta: String
    val expiryWriteoffSelectedLabel: (Int) -> String
    val expiryCountNoun: String
    val expiryTotalRemaining: String
    val expiryHeaderDrugName: String
    val expiryHeaderLotNumber: String
    val expiryHeaderExpiry: String
    val expiryHeaderRemaining: String
    val expiryStatusExpired: String
    val expiryStatusDaysLeft: (Int) -> String
    val expiryEmpty: String
    val expiryConfirmTitle: String
    val expiryConfirmMessage: (Int) -> String
    val expiryResultSuccessTitle: String
    val expiryResultPartialTitle: String
    val expiryResultSummary: (Int, Int) -> String
    val movementsSubtitle: String
    val movementsSearchPlaceholder: String
    val movementsCountNoun: String
    val movementsEmpty: String
    val movementsHeaderType: String
    val movementsHeaderRef: String
    val movementsHeaderBy: String
    val movementsPrevPage: String
    val movementsNextPage: String
    val movementsPagination: (Int, Int) -> String
    val movementsShownOf: (Int, Int) -> String
    val labelsSubtitle: String
    val labelsSearchPlaceholder: String
    val labelsEmpty: String
    val labelsListTitle: (Int) -> String
    val labelsRemoveLine: String
    val labelsClear: String
    val labelsSizeLabel: String
    val labelsPreviewLabel: (String) -> String
    val labelsPrintCount: (Int) -> String
    val labelsPrinting: String
    val labelsPrintSuccess: String
    val labelsTotalPrice: String
    val labelsLotPrefix: String
    val labelsLotUnspecified: String
    val helpSubtitle: String
    val helpToc: String
    val helpNotFound: String
    val helpTipsLabel: String
    val helpTipFocusSearch: String
    val helpTipPaymentField: String
    val helpTipParkBill: String
    val planningTitle: String
    val planningRefreshCta: String
    val planningAddPoCta: String
    val planningLowStockTitle: String
    val planningBelowMinTitle: String
    val planningReorderTitle: String
    val planningLowStockEmpty: String
    val planningBelowMinEmpty: String
    val planningReorderEmpty: String
    val planningReorderEmptyTitle: String
    val planningHeaderMin: String
    val planningHeaderRecommend: String
    val planningHeaderTotalCost: String
    val planningCountNoun: String
    val planningMetaLine: (String, String) -> String
    val planningDaysLeftLabel: (Int) -> String
    val bulkImportTitle: String
    val bulkImportSubtitle: String
    val bulkImportDropZoneHint: String
    val bulkImportDropZonePickFile: String
    val bulkImportSupportsHint: String
    val bulkImportPasteHere: String
    val bulkImportPasteHint: String
    val bulkImportDownloadTemplate: String
    val bulkImportValidateCta: String
    val bulkImportValidatePromptHint: String
    val bulkImportValidatedReady: (Int) -> String
    val bulkImportImportAllCta: String
    val bulkImportEmptyDropped: String
    val bulkImportEmptyDefault: String
    val bulkImportHeaderGeneric: String
    val bulkImportStatusReady: String
    val bulkImportStatusError: String
    val bulkImportResultTitle: (Int) -> String
    val bulkImportResultAllSuccess: String
    val bulkImportResultPartial: String
    val bulkImportResultAllFail: String
    val bulkImportResultSummary: (Int, Int) -> String
    val bulkImportResultSuccessLabel: String
    val bulkImportClearCta: String
    val stockCountHistoryTitle: String
    val stockCountHistorySubtitle: String
    val stockCountHistorySearchPlaceholder: String
    val stockCountHistoryNewCta: String
    val stockCountHistoryEmpty: String
    val stockCountHistoryNotFound: String
    val stockCountHistoryCountNoun: String
    val stockCountHeaderRound: String
    val stockCountHeaderItems: String
    val stockCountHeaderDelta: String
    val stockCountHeaderAdjust: String
    val stockCountStatusAdjusted: String
    val stockCountStatusNotAdjusted: String
    val stockCountHeaderNote: String
    val stockCountActionDetails: String
    val stockCountFormSearchPlaceholder: String
    val stockCountFormNotePlaceholder: String
    val stockCountFormCounted: String
    val stockCountFormInSystem: String
    val stockCountFormDelta: String
    val stockCountFormUnitLabel: (String) -> String
    val stockCountFormChangedItems: String
    val stockCountFormFillSystem: String
    val stockCountFormSummaryAll: String
    val stockCountFormSummaryAdjusted: String
    val stockCountFormSummaryNotAdjusted: String
    val stockCountFormPrintedShort: String
    val stockCountFormCounted2: String
    val stockCountFormDiscrepancyTotal: String
    val stockCountFormTopDiscrepancy: (Int) -> String
    val stockCountFormClearDraftCta: String
    val stockCountFormSaveRoundCta: String
    val stockCountFormResultLine: (Int, Int, Int) -> String
    val stockCountFormStatusLine: (Int, Int, Int, Int) -> String
    val stockCountFormSaveCountLabel: (Int) -> String
    val stockCountFormSummaryDelta: (Int, Int) -> String
    val stockCountFormEmptySearching: String
    val stockCountFormEmptyDefault: String
    val stockCountFormConfirmTitle: String
    val stockCountFormConfirmMessage: String
    val stockCountFormConfirmCta: String
    val importsTitle: String
    val importsSubtitle: String
    val importsSearchPlaceholder: String
    val importsCountNoun: String
    val importsAddCta: String
    val importsListEmpty: String
    val importsListNotFound: String
    val importsHeaderDocNo: String
    val importsHeaderSupplier: String
    val importsHeaderTotal: String
    val importsHeaderCreatedAt: String
    val importsStatusDraft: String
    val importsStatusReceived: String
    val importsStatusReceivedDetail: String
    val importsActionView: String
    val importsActionConfirmReceive: String
    val importsActionAddLine: String
    val importsActionRemoveLine: String
    val importsFormInfoSection: String
    val importsFormDocNo: String
    val importsFormDocNoPlaceholder: String
    val importsFormSupplier: String
    val importsFormSupplierPlaceholder: String
    val importsFormSupplierPickerTitle: String
    val importsFormSupplierSearchPlaceholder: String
    val importsFormReceiveDate: String
    val importsFormCreatedAt: String
    val importsFormConfirmedAt: String
    val importsItemListLabel: String
    val importsFormItemListTitle: (Int) -> String
    val importsFormItemTotalLabel: String
    val importsFormItemTotal: (String) -> String
    val importsFormItemLotLine: (String, String) -> String
    val importsFormPickDrug: String
    val commonPick: String
    val commonBaht: String
    val importsFormPickDrugPlaceholder: String
    val importsFormPickDrugTitle: String
    val importsFormPickDrugSearchPlaceholder: String
    val importsFormHeaderLotNumber: String
    val importsFormHeaderLotNumberPlaceholder: String
    val importsFormHeaderExpiry: String
    val importsExpiryDateLabel: String
    val importsFormHeaderCostPrice: String
    val importsFormHeaderSellPrice: String
    val importsFormHeaderOptions: String
    val importsFormReceivedAll: String
    val importsFormEditTitle: String
    val importsNewTitle: String
    val importsFormReceivedBadge: String
    val importsFormReceivedConfirmedHint: String
    val importsConfirmReceiveTitle: String
    val importsConfirmReceiveSubtitle: String
    val importsConfirmReceiveMessage: String
    val importsConfirmReceiveCta: String
    val importsConfirmDeleteDraftTitle: String
    val importsConfirmDeleteDraftMessage: String
    val importsConfirmDeleteReceivedTitle: String
    val importsConfirmDeleteReceivedMessage: String
    val importsHeaderInvoiceNo: String
    val importsHeaderInvoicePlaceholder: String
    val reportsSubtitle: String
    val reportsTabSummary: String
    val reportsTabProfit: String
    val reportsTabEod: String
    val reportsMetricSalesToday: String
    val reportsMetricSalesMonth: String
    val reportsMetricProfitMonthApprox: String
    val reportsMetricProfitMonthHint: String
    val reportsMetricStockValue: String
    val reportsMetricStockHint: (Int, Int) -> String
    val reportsRangeToday: String
    val reportsRangeThisWeek: String
    val reportsRangeThisMonth: String
    val reportsRangeLastMonth: String
    val reportsEmptyDay: String
    val reportsEmptyNoBills: String
    val reportsEmptyNoData: String
    val reportsEmptyChartHint: String
    val reportsSectionDailySales: String
    val reportsSectionDailySalesEmpty: String
    val reportsSectionTopBills: String
    val reportsSectionMonthly: String
    val reportsAvgPerDay: (String) -> String
    val reportsHeaderQtySold: String
    val reportsHeaderBills: String
    val reportsHeaderRevenue: String
    val reportsHeaderCost: String
    val reportsHeaderProfit: String
    val reportsHeaderDrugName: String
    val reportsTotalLabel: String
    val reportsRevenueVsCostLabel: String
    val reportsProfitTitle: String
    val reportsProfitSubtitle: String
    val reportsProfitTotal: String
    val reportsProfitBeforeCost: String
    val reportsProfitRevenue: String
    val reportsProfitCost: String
    val reportsTopSellingTitle: String
    val reportsProfitTopSelling: String
    val reportsProfitHighMargin: String
    val reportsProfitNoMovement: String
    val reportsProfitSetCostHint: String
    val reportsProfitMissingCostBanner: (Int) -> String
    val reportsProfitLossExample: String
    val reportsRevenueMinusCost: String
    val reportsCostBasis: String
    val reportsDrugWord: String
    val reportsSortBy: String
    val reportsDatePlaceholder: String
    val reportsEodTitle: String
    val reportsEodSubtitle: String
    val reportsEodDate: String
    val reportsEodToday: String
    val reportsEodConfirmTitle: String
    val reportsEodConfirmMessage: String
    val reportsEodCloseCta: String
    val reportsEodClosedBadge: String
    val reportsEodTryDifferentRange: String
    val reportsEodTryAnotherDate: String
    val reportsEodPrintCta: String
    val reportsEodChannelSum: String
    val reportsEodDayTotal: String
    val reportsEodNetSalesLabel: String
    val reportsEodTotalDiscount: String
    val reportsEodCashIn: String
    val reportsEodCashReceived: String
    val reportsEodChangeOut: String
    val reportsEodReceiveMinusChange: String
    val reportsEodDrawerMatches: String
    val reportsEodDrawerMismatches: String
    val reportsEodNetSalesLine: (String, Int) -> String
    val reportsEodNetSalesAndCashLine: (String, String) -> String
    val reportsEodCashLine: (String) -> String
    val reportsEodClosedDate: (String) -> String
    val reportsBillsOfDay: (Int) -> String
    val reportsWalkInCustomer: String
}

object PharmStringsTh : PharmStrings {
    override val commonCancel = "ยกเลิก"
    override val commonSave = "บันทึก"
    override val commonDelete = "ลบ"
    override val commonEdit = "แก้ไข"
    override val commonAdd = "เพิ่ม"
    override val commonSearch = "ค้นหา"
    override val commonConfirm = "ยืนยัน"
    override val commonClose = "ปิด"
    override val commonBack = "กลับ"
    override val commonLoading = "กำลังโหลด"
    override val commonRetry = "ลองอีกครั้ง"
    override val commonMore = "เพิ่มเติม"
    override val commonTotal = "ทั้งหมด"
    override val commonQty = "จำนวน"
    override val commonUnit = "หน่วย"
    override val commonPrice = "ราคา"
    override val commonStatus = "สถานะ"
    override val commonNote = "หมายเหตุ"
    override val commonName = "ชื่อ"
    override val commonPhone = "เบอร์โทร"
    override val commonDate = "วันที่"
    override val commonFilter = "ตัวกรอง"
    override val commonExport = "ส่งออก"
    override val commonPrint = "พิมพ์"
    override val commonRefresh = "รีเฟรช"
    override val commonUnitPiece = "ชิ้น"
    override val commonUnitTablet = "เม็ด"
    override val commonUnitCapsule = "แคปซูล"
    override val commonRequired = "จำเป็น"
    override val commonOptional = "ไม่บังคับ"
    override val commonYes = "ใช่"
    override val commonNo = "ไม่"
    override val settingsLocaleTitle = "ภาษา"
    override val settingsLocaleTh = "ไทย"
    override val settingsLocaleEn = "English"
    override val settingsLocaleAppliedInline = "เปลี่ยนภาษาแล้ว"
    override val settingsLocaleRestartHint = "บางส่วน (เช่น ปฏิทิน) จะเปลี่ยนหลังจากเปิดแอปใหม่"
    override val loginUsernameLabel = "ชื่อผู้ใช้"
    override val loginUsernamePlaceholder = "กรอกชื่อผู้ใช้"
    override val loginPasswordLabel = "รหัสผ่าน"
    override val loginPasswordPlaceholder = "กรอกรหัสผ่าน"
    override val loginSubmit = "เข้าสู่ระบบ"
    override val loginSubmitting = "กำลังเข้าสู่ระบบ…"
    override val loginBrandName = "ร้านยา เฮลท์ตี้ฟาร์ม"
    override val loginBrandTagline = "ระบบ POS ร้านขายยา"
    override val loginVersionPrefix = "เชื่อมต่อกับ Um-Api"
    override val navSell = "หน้าขายยา"
    override val navSalesHistory = "ประวัติการขาย"
    override val navStock = "สต็อกยา"
    override val navStockCounts = "ตรวจนับสต็อก"
    override val navExpiry = "จัดการวันหมดอายุ"
    override val navLabelPrint = "พิมพ์ฉลาก"
    override val navMovements = "ความเคลื่อนไหวสต็อก"
    override val navOfflineSync = "รายการค้างซิงค์"
    override val navImports = "นำเข้าสินค้า"
    override val navSuppliers = "ซัพพลายเออร์"
    override val navCustomers = "ลูกค้า"
    override val navReports = "รายงาน"
    override val navProfit = "กำไร"
    override val navKyForms = "แบบฟอร์ม ขย. 9–12"
    override val navUsers = "จัดการผู้ใช้งาน"
    override val navSettings = "ตั้งค่าระบบ"
    override val navHelp = "คู่มือการใช้งาน"
    override val titleSell = "ขายยา"
    override val profileTitle = "โปรไฟล์ของฉัน"
    override val profileSectionPersonal = "ข้อมูลส่วนตัว"
    override val profileSectionPersonalSubtitle = "แก้ไขชื่อ, เบอร์โทร และอีเมลของบัญชีคุณ"
    override val profileSectionPassword = "เปลี่ยนรหัสผ่าน"
    override val profileSectionPasswordSubtitle = "รหัสใหม่ต้องไม่น้อยกว่า 8 ตัวอักษร"
    override val profileSectionDisplay = "การแสดงผล"
    override val profileSectionDisplaySubtitle = "ปรับสำหรับเครื่องนี้เท่านั้น"
    override val profileFirstName = "ชื่อ"
    override val profileLastName = "นามสกุล"
    override val profileEmail = "อีเมล"
    override val profileSavedInline = "บันทึกแล้ว"
    override val profileSaving = "กำลังบันทึก…"
    override val profilePasswordIntro = "ตั้งรหัสผ่านใหม่เพื่อความปลอดภัย"
    override val profilePasswordOld = "รหัสผ่านเดิม"
    override val profilePasswordNew = "รหัสผ่านใหม่"
    override val profilePasswordConfirm = "ยืนยันรหัสผ่านใหม่"
    override val profilePasswordMismatch = "ไม่ตรงกับรหัสผ่านใหม่"
    override val profilePasswordChange = "เปลี่ยน"
    override val profilePasswordChanging = "กำลังเปลี่ยน…"
    override val profilePasswordChanged = "เปลี่ยนรหัสผ่านสำเร็จแล้ว"
    override val profilePasswordChangeFailed = "เปลี่ยนรหัสผ่านไม่สำเร็จ"
    override val profileDisplayTheme = "ธีม"
    override val profileDisplayFontSize = "ขนาดตัวอักษร"
    override val profileDisplayDensity = "ความหนาแน่นตาราง"
    override val profileThemeLight = "สว่าง"
    override val profileThemeDark = "มืด"
    override val profileThemeAuto = "อัตโนมัติ"
    override val profileFontSm = "เล็ก"
    override val profileFontMd = "ปกติ"
    override val profileFontLg = "ใหญ่"
    override val profileFontXl = "ใหญ่มาก"
    override val profileDensityComfortable = "สบายตา"
    override val profileDensityCompact = "กระชับ"
    override val settingsToolbarSubtitle = "จัดการข้อมูลร้าน ใบเสร็จ สต็อก เภสัชกร และ ขย."
    override val settingsDirtySubtitle = "มีการเปลี่ยนแปลง — แตะ \"บันทึก\" เพื่อยืนยัน"
    override val settingsTabStore = "ร้านค้า"
    override val settingsTabReceipt = "ใบเสร็จ"
    override val settingsTabStock = "สต็อก"
    override val settingsTabPharmacist = "เภสัชกร"
    override val settingsTabKy = "ขย."
    override val settingsStoreNameLabel = "ชื่อร้าน *"
    override val settingsStoreNamePlaceholder = "เช่น ร้านยาดี"
    override val settingsStoreAddress = "ที่อยู่"
    override val settingsStoreTaxId = "เลขผู้เสียภาษี"
    override val settingsStoreTimezone = "เขตเวลา (IANA)"
    override val settingsReceiptHeader = "ข้อความบนหัวบิล"
    override val settingsReceiptHeaderPlaceholder = "ปรากฏใต้ชื่อร้านในใบเสร็จ"
    override val settingsReceiptFooter = "ข้อความท้ายบิล"
    override val settingsReceiptFooterPlaceholder = "เช่น ขอบคุณที่ใช้บริการ"
    override val settingsReceiptFooterHint = "ปรากฏที่ส่วนล่างของใบเสร็จ"
    override val settingsReceiptPaperWidth = "ความกว้างกระดาษ"
    override val settingsReceiptShowPharmacist = "แสดงชื่อเภสัชกร"
    override val settingsStockLowThresholdLabel = "เกณฑ์สต็อกขั้นต่ำ (ของยาที่ไม่ระบุ min_stock)"
    override val settingsStockLowThresholdPlaceholder = "0 = ไม่แจ้งเตือน"
    override val settingsStockReorderDays = "ช่วงเวลาวิเคราะห์ Reorder (วัน)"
    override val settingsStockReorderLookahead = "Lookahead เป้า cover (วัน)"
    override val settingsStockExpiringDays = "ช่วงเตือนใกล้หมดอายุ (วัน)"
    override val settingsPharmacistName = "ชื่อเภสัชกร"
    override val settingsPharmacistLicenseNo = "เลขที่ใบประกอบวิชาชีพ"
    override val settingsKySkipAuto = "ข้ามการบันทึก ขย. อัตโนมัติ"
    override val settingsKySkipAutoHint = "เมื่อเปิด ผู้ขายจะข้าม KyCaptureSheet ไปออกบิลทันที"
    override val settingsKyDefaultBuyerAddress = "ที่อยู่ผู้ซื้อเริ่มต้น (ขย.10)"
    override val settingsKyDefaultBuyerAddressHint = "ใช้เป็นค่าเริ่มต้นเมื่อเปิด KyCaptureSheet"
    override val customersListSubtitle = "จัดการข้อมูลลูกค้าและประวัติการซื้อ"
    override val customersSearchPlaceholder = "ค้นหาชื่อ / เบอร์โทร…"
    override val customersAddCta = "เพิ่มลูกค้า"
    override val customersCountNoun = "ราย"
    override val customersHeaderAllergyShort = "โรคประจำตัว / แพ้ยา"
    override val customersHeaderTotalSpent = "ยอดซื้อรวม"
    override val customersHeaderLastVisit = "มาล่าสุด"
    override val customersHeaderActions = "จัดการ"
    override val customersListNotFound = "ไม่พบลูกค้าตามที่ค้นหา"
    override val customersListEmpty = "ยังไม่มีรายชื่อลูกค้า"
    override val customersActionHistory = "ประวัติ"
    override val customersDetailNoPhone = "ไม่ระบุเบอร์โทร"
    override val customersAllergyLabel = "แพ้ยา / โรคประจำตัว"
    override val customersDetailNoSales = "ลูกค้ารายนี้ยังไม่มีบิล"
    override val customersBadgeVoided = "ยกเลิกแล้ว"
    override val customersFormEditTitle = "แก้ไขลูกค้า"
    override val customersFormInfoSection = "ข้อมูลลูกค้า"
    override val customersFormFullName = "ชื่อ-นามสกุล"
    override val customersFormNamePlaceholder = "เช่น สมศรี ใจดี"
    override val customersFormAllergyHint = "จะถูกแสดงเป็นแถบเตือนสีแดงในตะกร้า"
    override val customersTierLabel = "กลุ่มราคา"
    override val customersTierHint = "กลุ่มราคาเริ่มต้นของลูกค้า เวลาขายระบบจะใช้ราคาตามกลุ่มนี้ ถ้ายาไม่มีราคากลุ่มนั้นจะใช้ราคาหน้าร้านแทน"
    override val customersTierRetail = "หน้าร้าน"
    override val customersTierRegular = "ทั่วไป"
    override val customersTierWholesale = "ส่ง"
    override val suppliersListSubtitle = "จัดการข้อมูลผู้ขายและบริษัทคู่ค้า"
    override val suppliersSearchPlaceholder = "ค้นหาชื่อ / ผู้ติดต่อ / เบอร์โทร…"
    override val suppliersAddCta = "เพิ่มซัพพลายเออร์"
    override val suppliersListNotFound = "ไม่พบซัพพลายเออร์ตามที่ค้นหา"
    override val suppliersListEmpty = "ยังไม่มีซัพพลายเออร์"
    override val suppliersHeaderName = "ชื่อบริษัท / ร้านค้า"
    override val suppliersHeaderContact = "ผู้ติดต่อ"
    override val suppliersHeaderTaxId = "เลขผู้เสียภาษี"
    override val suppliersHeaderDetails = "รายละเอียด"
    override val suppliersDeleteConfirmTitle = "ลบซัพพลายเออร์?"
    override val suppliersDeleteConfirmMessage: (String) -> String = { name ->
        "ต้องการลบ \"$name\" ออกจากระบบหรือไม่ — ใบรับสินค้าเดิมจะยังคงเก็บชื่อนี้ไว้"
    }
    override val suppliersFormAddTitle = "เพิ่มผู้จัดจำหน่าย"
    override val suppliersFormEditTitle = "แก้ไขผู้จัดจำหน่าย"
    override val suppliersFormInfoSection = "ข้อมูลผู้จัดจำหน่าย"
    override val suppliersFormCompanyName = "ชื่อบริษัท / ผู้จัดจำหน่าย"
    override val suppliersFormCompanyPlaceholder = "เช่น บริษัท เอ บี ซี ฟาร์มา จำกัด"
    override val suppliersFormContactName = "ชื่อพนักงานขาย"
    override val suppliersFormAddress = "ที่อยู่"
    override val suppliersFormAddressPlaceholder = "บ้านเลขที่ / ถนน / ตำบล / อำเภอ / จังหวัด"
    override val suppliersFormTaxId = "เลขประจำตัวผู้เสียภาษี"
    override val suppliersFormNotesPlaceholder = "เงื่อนไขการสั่งซื้อ / ส่วนลด / รายละเอียดเพิ่มเติม"
    override val usersListSubtitle = "บัญชีผู้ใช้ในระบบ User Management"
    override val usersSearchPlaceholder = "ค้นหาชื่อ / username / อีเมล…"
    override val usersAddCta = "เพิ่มผู้ใช้งาน"
    override val usersAddFirstCta = "เพิ่มผู้ใช้งานคนแรก"
    override val usersCountNoun = "คน"
    override val usersOwnAccountBadge = "บัญชีของคุณ"
    override val usersListEmpty = "ยังไม่มีผู้ใช้งาน"
    override val usersListNotFound = "ไม่พบผู้ใช้งานที่ค้นหา"
    override val usersCannotEdit = "ไม่สามารถแก้ไขได้"
    override val usersHeaderName = "ชื่อ-นามสกุล"
    override val usersStatusActive = "เปิดใช้งาน"
    override val usersStatusSuspended = "ระงับ"
    override val usersActionChangeRole = "เปลี่ยน Role"
    override val usersActionSetPassword = "ตั้งรหัสผ่าน"
    override val usersActionSuspend = "ระงับ"
    override val usersActionEnable = "เปิดใช้"
    override val usersConfirmDeleteTitle = "ยืนยันลบผู้ใช้งาน"
    override val usersConfirmDeleteMessage: (String) -> String = { name ->
        "ลบผู้ใช้งาน \"$name\" ?\nการดำเนินการนี้ไม่สามารถกู้คืนได้"
    }
    override val usersConfirmRoleTitle = "เปลี่ยน Role"
    override val usersConfirmEnableTitle = "ยืนยันเปิดใช้งาน"
    override val usersConfirmSuspendTitle = "ยืนยันระงับการใช้งาน"
    override val usersConfirmEnableMessage: (String) -> String = { name -> "เปิดใช้งานผู้ใช้ \"$name\"" }
    override val usersConfirmSuspendMessage: (String) -> String = { name -> "ระงับผู้ใช้ \"$name\"" }
    override val usersSetPasswordTitle: (String) -> String = { name -> "ตั้งรหัสผ่าน — $name" }
    override val usersFormAddTitle = "เพิ่มผู้ใช้งาน"
    override val usersFormEditTitle = "แก้ไขผู้ใช้งาน"
    override val usersFormInfoSection = "ข้อมูลผู้ใช้"
    override val usersFormUsername = "ชื่อผู้ใช้"
    override val usersFormPasswordCreate = "รหัสผ่าน (≥8 ตัว)"
    override val usersFormPasswordNew = "รหัสผ่านใหม่ (≥8 ตัว)"
    override val usersFormPasswordHint = "รหัสผ่านต้องไม่น้อยกว่า 8 ตัวอักษร"
    override val salesHistorySubtitle = "บิลขายย้อนหลังและการคืน/ยกเลิก"
    override val salesHistorySearchPlaceholder = "เลขบิล หรือ ชื่อลูกค้า…"
    override val salesHistoryCountNoun = "บิล"
    override val salesHistoryEmptySearching = "ไม่พบบิลที่ค้นหา"
    override val salesHistoryEmptyDateRange = "ไม่พบบิลในช่วงเวลาที่เลือก"
    override val salesHistoryHeaderTime = "เวลา"
    override val salesHistoryHeaderBillNo = "เลขที่บิล"
    override val salesHistoryHeaderNet = "ยอดสุทธิ"
    override val salesHistoryStatsTotal = "ยอดรวม"
    override val salesHistoryWalkInCustomer = "ลูกค้าทั่วไป"
    override val salesHistoryStatusOk = "สำเร็จ"
    override val salesHistoryActionViewBill = "ดูบิล"
    override val salesHistoryActionReturn = "คืนยา"
    override val offlineSyncSubtitle = "ตรวจสอบบิล offline ที่ยังไม่ได้ส่งเข้า backend"
    override val offlineSyncRetryAllCta = "ลองซิงก์ทั้งหมด"
    override val offlineSyncEmptyTitle = "ไม่มีบิลค้างซิงก์"
    override val offlineSyncEmpty = "ทุกบิลส่งเข้า backend แล้ว"
    override val offlineSyncMetricsTotal = "รายการค้างทั้งหมด"
    override val offlineSyncMetricsLocation = "ใน IndexedDB"
    override val offlineSyncMetricsAttempts = "ความพยายามสะสม"
    override val offlineSyncMetricsAttemptsSuffix = "ครั้งสะสม"
    override val offlineSyncMetricsFailed = "ซิงก์ล้มเหลว"
    override val offlineSyncStatusFailed = "ล้มเหลว"
    override val offlineSyncStatusPending = "รอซิงก์"
    override val offlineSyncStatusRetry = "รอ retry"
    override val offlineSyncAttemptsLabel: (Int) -> String = { attempts -> "ลอง $attempts ครั้ง" }
    override val offlineSyncRetryRowCta = "ลองส่งใหม่"
    override val offlineSyncDeleteConfirmTitle = "ลบรายการค้างซิงก์?"
    override val offlineSyncDeleteConfirmMessage =

        "บิลนี้จะถูกลบออกจากคิวภายในเครื่อง — ใช้เมื่อแน่ใจว่า " +
        "backend รับบิลนี้ไปแล้วหรือไม่ต้องการให้ส่งซ้ำอีก"
    override val expirySubtitle = "ตรวจล็อตใกล้หมดอายุ และตัดจำหน่าย"
    override val expirySelectAll = "เลือกทั้งหมด"
    override val expirySelectPartial = "เลือกบางส่วน · กดเพื่อล้าง"
    override val expiryWriteoffCta = "ตัดจำหน่าย"
    override val expiryWriteoffSelectedLabel: (Int) -> String = { count -> "เขียนทิ้ง $count รายการ" }
    override val expiryCountNoun = "ล็อต"
    override val expiryTotalRemaining = "คงเหลือรวม"
    override val expiryHeaderDrugName = "ชื่อยา"
    override val expiryHeaderLotNumber = "เลขล็อต"
    override val expiryHeaderExpiry = "วันหมดอายุ"
    override val expiryHeaderRemaining = "คงเหลือ"
    override val expiryStatusExpired = "หมดอายุแล้ว"
    override val expiryStatusDaysLeft: (Int) -> String = { days -> "อีก $days วัน" }
    override val expiryEmpty = "ไม่มีล็อตในช่วงเวลานี้"
    override val expiryConfirmTitle = "ตัดจำหน่ายล็อต?"
    override val expiryConfirmMessage: (Int) -> String = { count ->
        "ระบบจะลบ $count ล็อต และลด stock ตาม remaining ของแต่ละล็อต — " +
        "บันทึกการตัดจำหน่ายไว้สำหรับตรวจสอบ"
    }
    override val expiryResultSuccessTitle = "ตัดจำหน่ายสำเร็จ"
    override val expiryResultPartialTitle = "ตัดจำหน่ายบางส่วน"
    override val expiryResultSummary: (Int, Int) -> String = { writtenOff, total -> "บันทึก $writtenOff/$total ล็อต" }
    override val movementsSubtitle = "ประวัติการเข้า-ออกของสต็อก"
    override val movementsSearchPlaceholder = "ค้นหาชื่อยา…"
    override val movementsCountNoun = "รายการ"
    override val movementsEmpty = "ไม่มีรายการในช่วงเวลานี้"
    override val movementsHeaderType = "ประเภท"
    override val movementsHeaderRef = "อ้างอิง"
    override val movementsHeaderBy = "โดย"
    override val movementsPrevPage = "‹ ก่อนหน้า"
    override val movementsNextPage = "ถัดไป ›"
    override val movementsPagination: (Int, Int) -> String = { page, total -> "หน้า $page / $total" }
    override val movementsShownOf: (Int, Int) -> String = { shown, total -> "แสดง $shown จาก $total รายการ" }
    override val labelsSubtitle = "ออกแบบและพิมพ์ฉลากยา"
    override val labelsSearchPlaceholder = "ค้นหายา (เพิ่มทีละบรรทัด)…"
    override val labelsEmpty = "ยังไม่มีรายการ เลือกยาทางซ้ายเพื่อเพิ่ม"
    override val labelsListTitle: (Int) -> String = { count -> "รายการฉลาก ($count บรรทัด)" }
    override val labelsRemoveLine = "ลบบรรทัด"
    override val labelsClear = "ล้าง"
    override val labelsSizeLabel = "ขนาดฉลาก"
    override val labelsPreviewLabel: (String) -> String = { size -> "ตัวอย่าง ($size)" }
    override val labelsPrintCount: (Int) -> String = { count -> "พิมพ์ $count ดวง" }
    override val labelsPrinting = "กำลังพิมพ์…"
    override val labelsPrintSuccess = "พิมพ์สำเร็จ"
    override val labelsTotalPrice = "รวมราคา"
    override val labelsLotPrefix = "ล็อต"
    override val labelsLotUnspecified = "(ไม่ระบุ)"
    override val helpSubtitle = "ระบบ POS ร้านขายยา · เลือกหัวข้อจากสารบัญทางซ้าย"
    override val helpToc = "สารบัญ"
    override val helpNotFound = "ไม่พบคู่มือ"
    override val helpTipsLabel = "เคล็ดลับ"
    override val helpTipFocusSearch = "โฟกัสช่องค้นหา"
    override val helpTipPaymentField = "ช่องรับเงิน"
    override val helpTipParkBill = "พักบิล"
    override val planningTitle = "คำแนะนำสั่งซื้อ"
    override val planningRefreshCta = "รีเฟรช"
    override val planningAddPoCta = "เพิ่มใบสั่งซื้อ"
    override val planningLowStockTitle = "ยาใกล้หมด"
    override val planningBelowMinTitle = "ยาที่ต่ำกว่าระดับสต็อกขั้นต่ำ"
    override val planningReorderTitle = "รายการที่แนะนำให้สั่งซื้อเพิ่ม"
    override val planningLowStockEmpty = "ไม่มียาใกล้หมด"
    override val planningBelowMinEmpty = "สต็อกยาทุกรายการสูงกว่าระดับขั้นต่ำ"
    override val planningReorderEmpty = "ยังไม่มียาที่ถึงเกณฑ์แนะนำให้สั่งซื้อเพิ่ม"
    override val planningReorderEmptyTitle = "ไม่มีรายการที่ต้องสั่งซื้อ"
    override val planningHeaderMin = "ขั้นต่ำ"
    override val planningHeaderRecommend = "แนะนำสั่ง"
    override val planningHeaderTotalCost = "ต้นทุนรวม"
    override val planningCountNoun = "รายการ"
    override val planningMetaLine: (String, String) -> String = { rate, daysLeft -> "ขายเฉลี่ย $rate/วัน · เหลือ $daysLeft" }
    override val planningDaysLeftLabel: (Int) -> String = { days -> "$days วัน" }
    override val bulkImportTitle = "นำเข้ายาด้วย JSON"
    override val bulkImportSubtitle = "อัปโหลดไฟล์ JSON หรือวางข้อความเพื่อสร้างยาทีเดียวหลายรายการ"
    override val bulkImportDropZoneHint = "ลากไฟล์ JSON มาวางที่นี่ หรือกดเลือกไฟล์"
    override val bulkImportDropZonePickFile = "เลือกไฟล์"
    override val bulkImportSupportsHint = "รองรับ array หรือ {\"drugs\": [...]} สูงสุด 1,000 รายการ"
    override val bulkImportPasteHere = "หรือวาง JSON ที่นี่"
    override val bulkImportPasteHint = "รองรับทั้ง array หรือ {\"drugs\": [...]}"
    override val bulkImportDownloadTemplate = "ดาวน์โหลด Template"
    override val bulkImportValidateCta = "ตรวจสอบ"
    override val bulkImportValidatePromptHint = "ตรวจสอบ JSON ก่อน"
    override val bulkImportValidatedReady: (Int) -> String = { count -> "ตรวจสอบแล้ว — พบ $count รายการ พร้อมนำเข้า" }
    override val bulkImportImportAllCta = "นำเข้าทั้งหมด"
    override val bulkImportEmptyDropped = "ไม่มีรายการให้นำเข้า"
    override val bulkImportEmptyDefault = "ยังไม่มีรายการ"
    override val bulkImportHeaderGeneric = "ยาสามัญ"
    override val bulkImportStatusReady = "พร้อมนำเข้า"
    override val bulkImportStatusError = "ผิดพลาด"
    override val bulkImportResultTitle: (Int) -> String = { count -> "ผลการนำเข้า · $count รายการ" }
    override val bulkImportResultAllSuccess = "นำเข้าสำเร็จทั้งหมด"
    override val bulkImportResultPartial = "นำเข้าบางส่วน"
    override val bulkImportResultAllFail = "นำเข้าไม่สำเร็จ"
    override val bulkImportResultSummary: (Int, Int) -> String = { imported, total -> "บันทึก $imported/$total รายการ" }
    override val bulkImportResultSuccessLabel = "สำเร็จ"
    override val bulkImportClearCta = "ล้าง"
    override val stockCountHistoryTitle = "ประวัติตรวจนับ"
    override val stockCountHistorySubtitle = "ประวัติรอบนับสต็อก และบันทึกการปรับยอด"
    override val stockCountHistorySearchPlaceholder = "ค้นหาเลขรอบ / หมายเหตุ…"
    override val stockCountHistoryNewCta = "นับสต็อกใหม่"
    override val stockCountHistoryEmpty = "ยังไม่มีรอบนับสต็อก"
    override val stockCountHistoryNotFound = "ไม่พบรอบนับตามที่ค้นหา"
    override val stockCountHistoryCountNoun = "รอบ"
    override val stockCountHeaderRound = "เลขรอบ"
    override val stockCountHeaderItems = "รายการ"
    override val stockCountHeaderDelta = "ส่วนต่าง"
    override val stockCountHeaderAdjust = "ปรับยอด"
    override val stockCountStatusAdjusted = "ปรับแล้ว"
    override val stockCountStatusNotAdjusted = "ไม่ปรับ"
    override val stockCountHeaderNote = "หมายเหตุ"
    override val stockCountActionDetails = "ดูรายละเอียด"
    override val stockCountFormSearchPlaceholder = "ค้นหายา / barcode…"
    override val stockCountFormNotePlaceholder = "เช่น ตรวจประจำเดือน…"
    override val stockCountFormCounted = "นับได้"
    override val stockCountFormInSystem = "ในระบบ"
    override val stockCountFormDelta = "ส่วนต่าง"
    override val stockCountFormUnitLabel: (String) -> String = { unit -> "หน่วย: $unit" }
    override val stockCountFormChangedItems = "รายการที่เปลี่ยน"
    override val stockCountFormFillSystem = "เติมตามระบบ"
    override val stockCountFormSummaryAll = "ทั้งหมด"
    override val stockCountFormSummaryAdjusted = "ปรับแล้ว"
    override val stockCountFormSummaryNotAdjusted = "ไม่ปรับ"
    override val stockCountFormPrintedShort = "พิมพ์แล้ว"
    override val stockCountFormCounted2 = "นับได้"
    override val stockCountFormDiscrepancyTotal = "ส่วนต่างรวม (abs)"
    override val stockCountFormTopDiscrepancy: (Int) -> String = { n -> "ส่วนต่างสูงสุด $n อันดับ" }
    override val stockCountFormClearDraftCta = "ล้าง draft"
    override val stockCountFormSaveRoundCta = "บันทึกรอบนี้"
    override val stockCountFormResultLine: (Int, Int, Int) -> String = { total, printed, counted -> "ทั้งหมด $total รายการ · พิมพ์แล้ว $printed · นับได้ $counted" }
    override val stockCountFormStatusLine: (Int, Int, Int, Int) -> String = { total, printed, changed, absDelta -> "ทั้งหมด $total รายการ · พิมพ์แล้ว $printed · แก้ไข $changed · ส่วนต่างรวม $absDelta" }
    override val stockCountFormSaveCountLabel: (Int) -> String = { count -> "บันทึก $count รายการ" }
    override val stockCountFormSummaryDelta: (Int, Int) -> String = { changed, total -> "แก้ไข $changed · ส่วนต่างรวม $total" }
    override val stockCountFormEmptySearching = "ไม่พบยาที่ค้นหา"
    override val stockCountFormEmptyDefault = "ยังไม่มีรายการยา"
    override val stockCountFormConfirmTitle = "ยืนยันการปรับสต็อก"
    override val stockCountFormConfirmMessage = "ระบบจะปรับสต็อกตามจำนวนที่นับ — ยืนยันแล้วไม่สามารถย้อนกลับได้"
    override val stockCountFormConfirmCta = "ยืนยัน"
    override val importsTitle = "นำเข้าสินค้า"
    override val importsSubtitle = "จัดการใบนำเข้า / รับสินค้าเข้าสต็อก"
    override val importsSearchPlaceholder = "ค้นหาเลขที่ / ผู้ขาย…"
    override val importsCountNoun = "ใบ"
    override val importsAddCta = "สร้างใบนำเข้า"
    override val importsListEmpty = "ยังไม่มีใบนำเข้า"
    override val importsListNotFound = "ไม่พบใบนำเข้าตามที่ค้นหา"
    override val importsHeaderDocNo = "เลขที่เอกสาร"
    override val importsHeaderSupplier = "ผู้ขาย"
    override val importsHeaderTotal = "มูลค่ารวม"
    override val importsHeaderCreatedAt = "สร้างเมื่อ"
    override val importsStatusDraft = "แบบร่าง"
    override val importsStatusReceived = "รับเข้าแล้ว"
    override val importsStatusReceivedDetail = "ยืนยันแล้ว — ใบนี้ถูกบันทึกในสต็อกเรียบร้อย"
    override val importsActionView = "ดู"
    override val importsActionConfirmReceive = "ยืนยันรับ"
    override val importsActionAddLine = "เพิ่มรายการ"
    override val importsActionRemoveLine = "ลบรายการ"
    override val importsFormInfoSection = "ข้อมูลใบรับสินค้า"
    override val importsFormDocNo = "เลขที่เอกสาร"
    override val importsFormDocNoPlaceholder = "เช่น A12345"
    override val importsFormSupplier = "ผู้จัดจำหน่าย"
    override val importsFormSupplierPlaceholder = "เช่น บริษัท เอ บี ซี ฟาร์มา"
    override val importsFormSupplierPickerTitle = "เลือกผู้จัดจำหน่าย"
    override val importsFormSupplierSearchPlaceholder = "ค้นหาชื่อ / ผู้ติดต่อ / เบอร์โทร"
    override val importsFormReceiveDate = "วันที่รับ"
    override val importsFormCreatedAt = "สร้างเมื่อ"
    override val importsFormConfirmedAt = "ยืนยันเมื่อ"
    override val importsItemListLabel = "รายการสินค้า"
    override val importsFormItemListTitle: (Int) -> String = { count -> "รายการสินค้า · $count รายการ" }
    override val importsFormItemTotalLabel = "รวม"
    override val importsFormItemTotal: (String) -> String = { amt -> "รวม $amt" }
    override val importsFormItemLotLine: (String, String) -> String = { lotNo, expiry -> "ล็อต $lotNo · หมดอายุ $expiry" }
    override val importsFormPickDrug = "เลือกยา"
    override val commonPick = "เลือก"
    override val commonBaht = "บาท"
    override val importsFormPickDrugPlaceholder = "เลือกยา…"
    override val importsFormPickDrugTitle = "เลือกยา"
    override val importsFormPickDrugSearchPlaceholder = "ค้นหาชื่อ / barcode"
    override val importsFormHeaderLotNumber = "ล็อตหมายเลข"
    override val importsFormHeaderLotNumberPlaceholder = "เช่น A12345"
    override val importsFormHeaderExpiry = "วันหมดอายุ"
    override val importsExpiryDateLabel = "วันหมดอายุ"
    override val importsFormHeaderCostPrice = "ราคาทุน"
    override val importsFormHeaderSellPrice = "ราคาขาย"
    override val importsFormHeaderOptions = "ออปชัน"
    override val importsFormReceivedAll = "รับของครบ"
    override val importsFormEditTitle = "แก้ไขใบรับสินค้า"
    override val importsNewTitle = "ใบรับสินค้าใหม่"
    override val importsFormReceivedBadge = "รับเข้าแล้ว"
    override val importsFormReceivedConfirmedHint = "ใบนี้ยืนยันแล้ว — แก้ไขไม่ได้"
    override val importsConfirmReceiveTitle = "ยืนยันรับสินค้า?"
    override val importsConfirmReceiveSubtitle = "ยืนยันรับสินค้าเข้าสต็อกตามใบนำเข้านี้หรือไม่ — รายการล็อตจะถูกบันทึกและไม่สามารถย้อนกลับได้"
    override val importsConfirmReceiveMessage = "เมื่อยืนยันแล้วระบบจะเพิ่มล็อต + อัปเดตสต็อก + บันทึก ขย.9 — ไม่สามารถยกเลิกได้"
    override val importsConfirmReceiveCta = "ยืนยันรับสินค้า"
    override val importsConfirmDeleteDraftTitle = "ลบใบนำเข้า?"
    override val importsConfirmDeleteDraftMessage = "ใบนี้ยังไม่ได้ยืนยัน — ลบแล้วจะไม่สามารถกู้คืนได้ "
    override val importsConfirmDeleteReceivedTitle = "ลบใบรับสินค้า?"
    override val importsConfirmDeleteReceivedMessage = "ต้องการลบใบนำเข้านี้ใช่หรือไม่ — ใบที่ยังไม่ได้ยืนยันเท่านั้นที่ลบได้"
    override val importsHeaderInvoiceNo = "เลขที่ Invoice"
    override val importsHeaderInvoicePlaceholder = "ใบส่งของ"
    override val reportsSubtitle = "ภาพรวมยอดขาย สต็อก และสินค้าขายดี"
    override val reportsTabSummary = "รายงานสรุป"
    override val reportsTabProfit = "กำไร"
    override val reportsTabEod = "ปิดรอบ EOD"
    override val reportsMetricSalesToday = "ยอดขายวันนี้"
    override val reportsMetricSalesMonth = "ยอดขายเดือนนี้"
    override val reportsMetricProfitMonthApprox = "กำไรเดือนนี้ (ประมาณ)"
    override val reportsMetricProfitMonthHint = "ประเมิน ~30% ของยอดขาย"
    override val reportsMetricStockValue = "มูลค่าสต็อก"
    override val reportsMetricStockHint: (Int, Int) -> String = { out, low -> "หมด $out / ใกล้หมด $low" }
    override val reportsRangeToday = "วันนี้"
    override val reportsRangeThisWeek = "สัปดาห์นี้"
    override val reportsRangeThisMonth = "เดือนนี้"
    override val reportsRangeLastMonth = "เดือนที่แล้ว"
    override val reportsEmptyDay = "ยังไม่มีข้อมูลของวันนี้"
    override val reportsEmptyNoBills = "ยังไม่มีบิล"
    override val reportsEmptyNoData = "ไม่มีข้อมูล"
    override val reportsEmptyChartHint = "ลองเลือกวันที่อื่นเพื่อดูยอดขาย"
    override val reportsSectionDailySales = "ยอดขายรายวัน"
    override val reportsSectionDailySalesEmpty = "ไม่มียอดขายในช่วงเวลานี้"
    override val reportsSectionTopBills = "บิลล่าสุด"
    override val reportsSectionMonthly = "รายได้ vs ต้นทุน — รายเดือน"
    override val reportsAvgPerDay: (String) -> String = { avg -> "เฉลี่ย $avg/วัน" }
    override val reportsHeaderQtySold = "จำนวนขาย"
    override val reportsHeaderBills = "จำนวนบิล"
    override val reportsHeaderRevenue = "รายได้"
    override val reportsHeaderCost = "ต้นทุน"
    override val reportsHeaderProfit = "กำไร"
    override val reportsHeaderDrugName = "ชื่อยา"
    override val reportsTotalLabel = "รวม"
    override val reportsRevenueVsCostLabel = "รวมทั้งวัน"
    override val reportsProfitTitle = "กำไรต่อยา"
    override val reportsProfitSubtitle = "กำไรแยกตามรายการยาในช่วงที่เลือก"
    override val reportsProfitTotal = "กำไรรวม"
    override val reportsProfitBeforeCost = "ก่อนหักต้นทุน"
    override val reportsProfitRevenue = "รายได้รวม"
    override val reportsProfitCost = "ต้นทุนรวม"
    override val reportsTopSellingTitle = "10 อันดับยาขายดี"
    override val reportsProfitTopSelling = "ขายมาก"
    override val reportsProfitHighMargin = "กำไรสูง"
    override val reportsProfitNoMovement = "ยาขายไม่ออก"
    override val reportsProfitSetCostHint = "ตั้งราคาทุน"
    override val reportsProfitMissingCostBanner: (Int) -> String = { count -> "$count รายการ มีรายได้แต่ไม่มีต้นทุน — กำไรอาจคลาดเคลื่อน คลิก \"ตั้งราคาทุน\" ในหน้าสต็อก" }
    override val reportsProfitLossExample = "ขาดทุนตัวอย่าง"
    override val reportsRevenueMinusCost = "รายได้ - ต้นทุน"
    override val reportsCostBasis = "ตามล็อตที่ตัด"
    override val reportsDrugWord = "ยา"
    override val reportsSortBy = "เรียงตาม"
    override val reportsDatePlaceholder = "รูปแบบ YYYY-MM-DD (ว่าง = วันนี้)"
    override val reportsEodTitle = "ปิดยอดสิ้นวัน"
    override val reportsEodSubtitle = "สรุปยอดขาย / ส่วนลด / เงินสดของวัน — ยืนยันก่อนปิดรอบ"
    override val reportsEodDate = "วันที่"
    override val reportsEodToday = "วันนี้"
    override val reportsEodConfirmTitle = "ยืนยันปิดยอด"
    override val reportsEodConfirmMessage = "ตรวจยอดให้ตรงก่อนยืนยัน — ปิดแล้วไม่สามารถย้อนกลับได้"
    override val reportsEodCloseCta = "ปิดยอด"
    override val reportsEodClosedBadge = "ปิดแล้ว"
    override val reportsEodTryDifferentRange = "ลองเปลี่ยนช่วงวันที่ด้านบน"
    override val reportsEodTryAnotherDate = "ลองเลือกวันที่อื่นเพื่อดูยอดขาย"
    override val reportsEodPrintCta = "พิมพ์"
    override val reportsEodChannelSum = "รวมทุกช่องทาง"
    override val reportsEodDayTotal = "รวมทั้งวัน"
    override val reportsEodNetSalesLabel = "ยอดขายสุทธิ"
    override val reportsEodTotalDiscount = "ส่วนลดรวม"
    override val reportsEodCashIn = "เงินเข้าลิ้นชัก"
    override val reportsEodCashReceived = "รับเงิน"
    override val reportsEodChangeOut = "ทอนเงิน"
    override val reportsEodReceiveMinusChange = "รับ − ทอน"
    override val reportsEodDrawerMatches = "ลิ้นชักตรงกับยอดขาย"
    override val reportsEodDrawerMismatches = "ลิ้นชักไม่ตรงกับยอดขาย"
    override val reportsEodNetSalesLine: (String, Int) -> String = { net, bills -> "ยอดขายสุทธิ $net · $bills บิล" }
    override val reportsEodNetSalesAndCashLine: (String, String) -> String = { cash, sales -> "เงินเข้าลิ้นชัก $cash · ยอดขาย $sales" }
    override val reportsEodCashLine: (String) -> String = { cash -> "เงินเข้าลิ้นชัก $cash" }
    override val reportsEodClosedDate: (String) -> String = { date -> "ปิดรอบ EOD เรียบร้อย — วันที่ $date" }
    override val reportsBillsOfDay: (Int) -> String = { count -> "บิลในวัน · $count รายการ" }
    override val reportsWalkInCustomer = "ลูกค้าทั่วไป"
}

object PharmStringsEn : PharmStrings {
    override val commonCancel = "Cancel"
    override val commonSave = "Save"
    override val commonDelete = "Delete"
    override val commonEdit = "Edit"
    override val commonAdd = "Add"
    override val commonSearch = "Search"
    override val commonConfirm = "Confirm"
    override val commonClose = "Close"
    override val commonBack = "Back"
    override val commonLoading = "Loading"
    override val commonRetry = "Retry"
    override val commonMore = "More"
    override val commonTotal = "Total"
    override val commonQty = "Qty"
    override val commonUnit = "Unit"
    override val commonPrice = "Price"
    override val commonStatus = "Status"
    override val commonNote = "Note"
    override val commonName = "Name"
    override val commonPhone = "Phone"
    override val commonDate = "Date"
    override val commonFilter = "Filter"
    override val commonExport = "Export"
    override val commonPrint = "Print"
    override val commonRefresh = "Refresh"
    override val commonUnitPiece = "pcs"
    override val commonUnitTablet = "tablet"
    override val commonUnitCapsule = "capsule"
    override val commonRequired = "Required"
    override val commonOptional = "Optional"
    override val commonYes = "Yes"
    override val commonNo = "No"
    override val settingsLocaleTitle = "Language"
    override val settingsLocaleTh = "ไทย"
    override val settingsLocaleEn = "English"
    override val settingsLocaleAppliedInline = "Language updated"
    override val settingsLocaleRestartHint = "Some parts (e.g. calendar) update after restarting the app"
    override val loginUsernameLabel = "Username"
    override val loginUsernamePlaceholder = "Enter your username"
    override val loginPasswordLabel = "Password"
    override val loginPasswordPlaceholder = "Enter your password"
    override val loginSubmit = "Sign in"
    override val loginSubmitting = "Signing in…"
    override val loginBrandName = "Healthy Pharm"
    override val loginBrandTagline = "Pharmacy POS"
    override val loginVersionPrefix = "Connected to Um-Api"
    override val navSell = "Sell"
    override val navSalesHistory = "Sales history"
    override val navStock = "Stock"
    override val navStockCounts = "Stock counts"
    override val navExpiry = "Expiry"
    override val navLabelPrint = "Print labels"
    override val navMovements = "Stock movements"
    override val navOfflineSync = "Pending sync"
    override val navImports = "Imports"
    override val navSuppliers = "Suppliers"
    override val navCustomers = "Customers"
    override val navReports = "Reports"
    override val navProfit = "Profit"
    override val navKyForms = "KY 9–12 forms"
    override val navUsers = "Users"
    override val navSettings = "Settings"
    override val navHelp = "User guide"
    override val titleSell = "Sell"
    override val profileTitle = "My profile"
    override val profileSectionPersonal = "Personal information"
    override val profileSectionPersonalSubtitle = "Edit your name, phone, and email"
    override val profileSectionPassword = "Change password"
    override val profileSectionPasswordSubtitle = "New password must be at least 8 characters"
    override val profileSectionDisplay = "Display"
    override val profileSectionDisplaySubtitle = "Applies to this device only"
    override val profileFirstName = "First name"
    override val profileLastName = "Last name"
    override val profileEmail = "Email"
    override val profileSavedInline = "Saved"
    override val profileSaving = "Saving…"
    override val profilePasswordIntro = "Set a new password for security"
    override val profilePasswordOld = "Current password"
    override val profilePasswordNew = "New password"
    override val profilePasswordConfirm = "Confirm new password"
    override val profilePasswordMismatch = "Does not match the new password"
    override val profilePasswordChange = "Change"
    override val profilePasswordChanging = "Changing…"
    override val profilePasswordChanged = "Password changed successfully"
    override val profilePasswordChangeFailed = "Password change failed"
    override val profileDisplayTheme = "Theme"
    override val profileDisplayFontSize = "Font size"
    override val profileDisplayDensity = "Table density"
    override val profileThemeLight = "Light"
    override val profileThemeDark = "Dark"
    override val profileThemeAuto = "Auto"
    override val profileFontSm = "Small"
    override val profileFontMd = "Normal"
    override val profileFontLg = "Large"
    override val profileFontXl = "Extra large"
    override val profileDensityComfortable = "Comfortable"
    override val profileDensityCompact = "Compact"
    override val settingsToolbarSubtitle = "Manage store, receipt, stock, pharmacist, and KY data"
    override val settingsDirtySubtitle = "Changes pending — tap \"Save\" to confirm"
    override val settingsTabStore = "Store"
    override val settingsTabReceipt = "Receipt"
    override val settingsTabStock = "Stock"
    override val settingsTabPharmacist = "Pharmacist"
    override val settingsTabKy = "KY"
    override val settingsStoreNameLabel = "Store name *"
    override val settingsStoreNamePlaceholder = "e.g. Good Pharmacy"
    override val settingsStoreAddress = "Address"
    override val settingsStoreTaxId = "Tax ID"
    override val settingsStoreTimezone = "Time zone (IANA)"
    override val settingsReceiptHeader = "Receipt header"
    override val settingsReceiptHeaderPlaceholder = "Shown below the store name on the receipt"
    override val settingsReceiptFooter = "Receipt footer"
    override val settingsReceiptFooterPlaceholder = "e.g. Thank you for your business"
    override val settingsReceiptFooterHint = "Shown at the bottom of the receipt"
    override val settingsReceiptPaperWidth = "Paper width"
    override val settingsReceiptShowPharmacist = "Show pharmacist name"
    override val settingsStockLowThresholdLabel = "Default low-stock threshold (for drugs without min_stock)"
    override val settingsStockLowThresholdPlaceholder = "0 = no alerts"
    override val settingsStockReorderDays = "Reorder analysis window (days)"
    override val settingsStockReorderLookahead = "Lookahead cover target (days)"
    override val settingsStockExpiringDays = "Near-expiry alert window (days)"
    override val settingsPharmacistName = "Pharmacist name"
    override val settingsPharmacistLicenseNo = "License number"
    override val settingsKySkipAuto = "Skip auto KY capture"
    override val settingsKySkipAutoHint = "When enabled, the cashier skips KyCaptureSheet and checks out immediately"
    override val settingsKyDefaultBuyerAddress = "Default buyer address (KY 10)"
    override val settingsKyDefaultBuyerAddressHint = "Used as the default when KyCaptureSheet opens"
    override val customersListSubtitle = "Manage customer data and purchase history"
    override val customersSearchPlaceholder = "Search name / phone…"
    override val customersAddCta = "Add customer"
    override val customersCountNoun = "entries"
    override val customersHeaderAllergyShort = "Conditions / Allergies"
    override val customersHeaderTotalSpent = "Total spent"
    override val customersHeaderLastVisit = "Last visit"
    override val customersHeaderActions = "Actions"
    override val customersListNotFound = "No customers match the search"
    override val customersListEmpty = "No customers yet"
    override val customersActionHistory = "History"
    override val customersDetailNoPhone = "No phone on file"
    override val customersAllergyLabel = "Allergies / Conditions"
    override val customersDetailNoSales = "This customer has no bills yet"
    override val customersBadgeVoided = "Voided"
    override val customersFormEditTitle = "Edit customer"
    override val customersFormInfoSection = "Customer info"
    override val customersFormFullName = "Full name"
    override val customersFormNamePlaceholder = "e.g. Somsri Jaidee"
    override val customersFormAllergyHint = "Shown as a red warning banner in the cart"
    override val customersTierLabel = "Price tier"
    override val customersTierHint = "Default price tier for this customer. Sales use this tier; if a drug has no tier price, the store price is used instead."
    override val customersTierRetail = "Store"
    override val customersTierRegular = "Regular"
    override val customersTierWholesale = "Wholesale"
    override val suppliersListSubtitle = "Manage suppliers and partner companies"
    override val suppliersSearchPlaceholder = "Search name / contact / phone…"
    override val suppliersAddCta = "Add supplier"
    override val suppliersListNotFound = "No suppliers match the search"
    override val suppliersListEmpty = "No suppliers yet"
    override val suppliersHeaderName = "Company / Store"
    override val suppliersHeaderContact = "Contact"
    override val suppliersHeaderTaxId = "Tax ID"
    override val suppliersHeaderDetails = "Details"
    override val suppliersDeleteConfirmTitle = "Delete supplier?"
    override val suppliersDeleteConfirmMessage: (String) -> String = { name ->
        "Delete \"$name\" from the system? Existing purchase orders will keep this name."
    }
    override val suppliersFormAddTitle = "Add supplier"
    override val suppliersFormEditTitle = "Edit supplier"
    override val suppliersFormInfoSection = "Supplier info"
    override val suppliersFormCompanyName = "Company / supplier name"
    override val suppliersFormCompanyPlaceholder = "e.g. ABC Pharma Co., Ltd."
    override val suppliersFormContactName = "Sales contact"
    override val suppliersFormAddress = "Address"
    override val suppliersFormAddressPlaceholder = "Street / sub-district / district / province"
    override val suppliersFormTaxId = "Tax identification number"
    override val suppliersFormNotesPlaceholder = "Order terms / discounts / additional details"
    override val usersListSubtitle = "User Management accounts"
    override val usersSearchPlaceholder = "Search name / username / email…"
    override val usersAddCta = "Add user"
    override val usersAddFirstCta = "Add the first user"
    override val usersCountNoun = "users"
    override val usersOwnAccountBadge = "Your account"
    override val usersListEmpty = "No users yet"
    override val usersListNotFound = "No users match the search"
    override val usersCannotEdit = "Cannot be edited"
    override val usersHeaderName = "Full name"
    override val usersStatusActive = "Active"
    override val usersStatusSuspended = "Suspended"
    override val usersActionChangeRole = "Change role"
    override val usersActionSetPassword = "Set password"
    override val usersActionSuspend = "Suspend"
    override val usersActionEnable = "Enable"
    override val usersConfirmDeleteTitle = "Confirm user deletion"
    override val usersConfirmDeleteMessage: (String) -> String = { name ->
        "Delete user \"$name\"?\nThis action cannot be undone."
    }
    override val usersConfirmRoleTitle = "Change role"
    override val usersConfirmEnableTitle = "Confirm enable"
    override val usersConfirmSuspendTitle = "Confirm suspension"
    override val usersConfirmEnableMessage: (String) -> String = { name -> "Enable user \"$name\"" }
    override val usersConfirmSuspendMessage: (String) -> String = { name -> "Suspend user \"$name\"" }
    override val usersSetPasswordTitle: (String) -> String = { name -> "Set password — $name" }
    override val usersFormAddTitle = "Add user"
    override val usersFormEditTitle = "Edit user"
    override val usersFormInfoSection = "User info"
    override val usersFormUsername = "Username"
    override val usersFormPasswordCreate = "Password (≥8 chars)"
    override val usersFormPasswordNew = "New password (≥8 chars)"
    override val usersFormPasswordHint = "Password must be at least 8 characters"
    override val salesHistorySubtitle = "Past sales, returns, and voids"
    override val salesHistorySearchPlaceholder = "Bill number or customer name…"
    override val salesHistoryCountNoun = "bills"
    override val salesHistoryEmptySearching = "No bills match the search"
    override val salesHistoryEmptyDateRange = "No bills in the selected period"
    override val salesHistoryHeaderTime = "Time"
    override val salesHistoryHeaderBillNo = "Bill no."
    override val salesHistoryHeaderNet = "Net total"
    override val salesHistoryStatsTotal = "Gross total"
    override val salesHistoryWalkInCustomer = "Walk-in"
    override val salesHistoryStatusOk = "Completed"
    override val salesHistoryActionViewBill = "View bill"
    override val salesHistoryActionReturn = "Return"
    override val offlineSyncSubtitle = "Offline bills not yet sent to the backend"
    override val offlineSyncRetryAllCta = "Sync all"
    override val offlineSyncEmptyTitle = "No pending sync items"
    override val offlineSyncEmpty = "All bills are synced with the backend"
    override val offlineSyncMetricsTotal = "Pending total"
    override val offlineSyncMetricsLocation = "In IndexedDB"
    override val offlineSyncMetricsAttempts = "Total attempts"
    override val offlineSyncMetricsAttemptsSuffix = "attempts"
    override val offlineSyncMetricsFailed = "Sync failed"
    override val offlineSyncStatusFailed = "Failed"
    override val offlineSyncStatusPending = "Pending sync"
    override val offlineSyncStatusRetry = "Awaiting retry"
    override val offlineSyncAttemptsLabel: (Int) -> String = { attempts -> "$attempts attempt(s)" }
    override val offlineSyncRetryRowCta = "Retry"
    override val offlineSyncDeleteConfirmTitle = "Delete pending sync item?"
    override val offlineSyncDeleteConfirmMessage =

        "This bill will be removed from the device queue — only do this if " +
        "the backend has already received it or you don't want to retry."
    override val expirySubtitle = "Check near-expiry lots and write off"
    override val expirySelectAll = "Select all"
    override val expirySelectPartial = "Partial selection · tap to clear"
    override val expiryWriteoffCta = "Write off"
    override val expiryWriteoffSelectedLabel: (Int) -> String = { count -> "Write off $count item(s)" }
    override val expiryCountNoun = "lots"
    override val expiryTotalRemaining = "Total remaining"
    override val expiryHeaderDrugName = "Drug name"
    override val expiryHeaderLotNumber = "Lot number"
    override val expiryHeaderExpiry = "Expiry date"
    override val expiryHeaderRemaining = "Remaining"
    override val expiryStatusExpired = "Expired"
    override val expiryStatusDaysLeft: (Int) -> String = { days -> "$days day(s) left" }
    override val expiryEmpty = "No lots in this window"
    override val expiryConfirmTitle = "Write off lots?"
    override val expiryConfirmMessage: (Int) -> String = { count ->
        "The system will remove $count lots and reduce stock by each lot's remaining — " +
        "the write-off is logged for audit."
    }
    override val expiryResultSuccessTitle = "Write-off complete"
    override val expiryResultPartialTitle = "Partially written off"
    override val expiryResultSummary: (Int, Int) -> String = { writtenOff, total -> "Recorded $writtenOff/$total lots" }
    override val movementsSubtitle = "Stock in/out history"
    override val movementsSearchPlaceholder = "Search drug name…"
    override val movementsCountNoun = "items"
    override val movementsEmpty = "No items in this period"
    override val movementsHeaderType = "Type"
    override val movementsHeaderRef = "Reference"
    override val movementsHeaderBy = "By"
    override val movementsPrevPage = "‹ Prev"
    override val movementsNextPage = "Next ›"
    override val movementsPagination: (Int, Int) -> String = { page, total -> "Page $page / $total" }
    override val movementsShownOf: (Int, Int) -> String = { shown, total -> "Showing $shown of $total items" }
    override val labelsSubtitle = "Design and print drug labels"
    override val labelsSearchPlaceholder = "Search drugs (add one per line)…"
    override val labelsEmpty = "No items yet — pick drugs on the left to add"
    override val labelsListTitle: (Int) -> String = { count -> "Label list ($count line(s))" }
    override val labelsRemoveLine = "Remove line"
    override val labelsClear = "Clear"
    override val labelsSizeLabel = "Label size"
    override val labelsPreviewLabel: (String) -> String = { size -> "Preview ($size)" }
    override val labelsPrintCount: (Int) -> String = { count -> "Print $count label(s)" }
    override val labelsPrinting = "Printing…"
    override val labelsPrintSuccess = "Printed"
    override val labelsTotalPrice = "Total price"
    override val labelsLotPrefix = "Lot"
    override val labelsLotUnspecified = "(unspecified)"
    override val helpSubtitle = "Pharmacy POS · pick a topic from the table of contents"
    override val helpToc = "Contents"
    override val helpNotFound = "Manual not found"
    override val helpTipsLabel = "Tips"
    override val helpTipFocusSearch = "Focus search field"
    override val helpTipPaymentField = "Receive payment field"
    override val helpTipParkBill = "Park bill"
    override val planningTitle = "Order recommendations"
    override val planningRefreshCta = "Refresh"
    override val planningAddPoCta = "Add PO"
    override val planningLowStockTitle = "Low stock"
    override val planningBelowMinTitle = "Below minimum stock"
    override val planningReorderTitle = "Suggested reorder list"
    override val planningLowStockEmpty = "No drugs running low"
    override val planningBelowMinEmpty = "All drugs above minimum stock"
    override val planningReorderEmpty = "No drugs reach reorder threshold yet"
    override val planningReorderEmptyTitle = "No items to order"
    override val planningHeaderMin = "Minimum"
    override val planningHeaderRecommend = "Recommend"
    override val planningHeaderTotalCost = "Total cost"
    override val planningCountNoun = "items"
    override val planningMetaLine: (String, String) -> String = { rate, daysLeft -> "Avg $rate/day · $daysLeft remaining" }
    override val planningDaysLeftLabel: (Int) -> String = { days -> "$days day(s)" }
    override val bulkImportTitle = "Bulk import drugs (JSON)"
    override val bulkImportSubtitle = "Upload a JSON file or paste text to create many drugs at once"
    override val bulkImportDropZoneHint = "Drop a JSON file here or pick one"
    override val bulkImportDropZonePickFile = "Pick file"
    override val bulkImportSupportsHint = "Accepts an array or {\"drugs\": [...]} — up to 1,000 items"
    override val bulkImportPasteHere = "Or paste JSON here"
    override val bulkImportPasteHint = "Accepts either an array or {\"drugs\": [...]}"
    override val bulkImportDownloadTemplate = "Download template"
    override val bulkImportValidateCta = "Validate"
    override val bulkImportValidatePromptHint = "Validate JSON first"
    override val bulkImportValidatedReady: (Int) -> String = { count -> "Validated — $count item(s) ready to import" }
    override val bulkImportImportAllCta = "Import all"
    override val bulkImportEmptyDropped = "No items to import"
    override val bulkImportEmptyDefault = "No items yet"
    override val bulkImportHeaderGeneric = "Generic"
    override val bulkImportStatusReady = "Ready"
    override val bulkImportStatusError = "Error"
    override val bulkImportResultTitle: (Int) -> String = { count -> "Import result · $count item(s)" }
    override val bulkImportResultAllSuccess = "Imported all"
    override val bulkImportResultPartial = "Partially imported"
    override val bulkImportResultAllFail = "Import failed"
    override val bulkImportResultSummary: (Int, Int) -> String = { imported, total -> "Recorded $imported/$total item(s)" }
    override val bulkImportResultSuccessLabel = "Success"
    override val bulkImportClearCta = "Clear"
    override val stockCountHistoryTitle = "Stock count history"
    override val stockCountHistorySubtitle = "Past stock-count rounds and adjustment records"
    override val stockCountHistorySearchPlaceholder = "Search round number / note…"
    override val stockCountHistoryNewCta = "New stock count"
    override val stockCountHistoryEmpty = "No stock-count rounds yet"
    override val stockCountHistoryNotFound = "No rounds match the search"
    override val stockCountHistoryCountNoun = "rounds"
    override val stockCountHeaderRound = "Round no."
    override val stockCountHeaderItems = "Items"
    override val stockCountHeaderDelta = "Delta"
    override val stockCountHeaderAdjust = "Adjustment"
    override val stockCountStatusAdjusted = "Adjusted"
    override val stockCountStatusNotAdjusted = "No change"
    override val stockCountHeaderNote = "Note"
    override val stockCountActionDetails = "View details"
    override val stockCountFormSearchPlaceholder = "Search drug / barcode…"
    override val stockCountFormNotePlaceholder = "e.g. Monthly stock check…"
    override val stockCountFormCounted = "Counted"
    override val stockCountFormInSystem = "In system"
    override val stockCountFormDelta = "Delta"
    override val stockCountFormUnitLabel: (String) -> String = { unit -> "Unit: $unit" }
    override val stockCountFormChangedItems = "Changed items"
    override val stockCountFormFillSystem = "Fill from system"
    override val stockCountFormSummaryAll = "All"
    override val stockCountFormSummaryAdjusted = "Adjusted"
    override val stockCountFormSummaryNotAdjusted = "Not adjusted"
    override val stockCountFormPrintedShort = "Printed"
    override val stockCountFormCounted2 = "Counted"
    override val stockCountFormDiscrepancyTotal = "Total delta (abs)"
    override val stockCountFormTopDiscrepancy: (Int) -> String = { n -> "Top $n discrepancies" }
    override val stockCountFormClearDraftCta = "Clear draft"
    override val stockCountFormSaveRoundCta = "Save this round"
    override val stockCountFormResultLine: (Int, Int, Int) -> String = { total, printed, counted -> "Total $total items · printed $printed · counted $counted" }
    override val stockCountFormStatusLine: (Int, Int, Int, Int) -> String = { total, printed, changed, absDelta -> "Total $total items · printed $printed · changed $changed · total delta $absDelta" }
    override val stockCountFormSaveCountLabel: (Int) -> String = { count -> "Save $count items" }
    override val stockCountFormSummaryDelta: (Int, Int) -> String = { changed, total -> "Changed $changed · total delta $total" }
    override val stockCountFormEmptySearching = "No drugs match the search"
    override val stockCountFormEmptyDefault = "No drugs yet"
    override val stockCountFormConfirmTitle = "Confirm stock adjustment"
    override val stockCountFormConfirmMessage = "The system will adjust stock to the counted quantity — once confirmed this cannot be undone"
    override val stockCountFormConfirmCta = "Confirm"
    override val importsTitle = "Imports"
    override val importsSubtitle = "Manage import documents / receive into stock"
    override val importsSearchPlaceholder = "Search doc no. / supplier…"
    override val importsCountNoun = "docs"
    override val importsAddCta = "New import"
    override val importsListEmpty = "No imports yet"
    override val importsListNotFound = "No imports match the search"
    override val importsHeaderDocNo = "Doc no."
    override val importsHeaderSupplier = "Supplier"
    override val importsHeaderTotal = "Total"
    override val importsHeaderCreatedAt = "Created"
    override val importsStatusDraft = "Draft"
    override val importsStatusReceived = "Received"
    override val importsStatusReceivedDetail = "Confirmed — this doc is recorded into stock"
    override val importsActionView = "View"
    override val importsActionConfirmReceive = "Confirm receipt"
    override val importsActionAddLine = "Add line"
    override val importsActionRemoveLine = "Remove line"
    override val importsFormInfoSection = "Receipt info"
    override val importsFormDocNo = "Document no."
    override val importsFormDocNoPlaceholder = "e.g. A12345"
    override val importsFormSupplier = "Supplier"
    override val importsFormSupplierPlaceholder = "e.g. ABC Pharma Co."
    override val importsFormSupplierPickerTitle = "Pick a supplier"
    override val importsFormSupplierSearchPlaceholder = "Search name / contact / phone"
    override val importsFormReceiveDate = "Receive date"
    override val importsFormCreatedAt = "Created"
    override val importsFormConfirmedAt = "Confirmed"
    override val importsItemListLabel = "Items"
    override val importsFormItemListTitle: (Int) -> String = { count -> "Items · $count line(s)" }
    override val importsFormItemTotalLabel = "Total"
    override val importsFormItemTotal: (String) -> String = { amt -> "Total $amt" }
    override val importsFormItemLotLine: (String, String) -> String = { lotNo, expiry -> "Lot $lotNo · expires $expiry" }
    override val importsFormPickDrug = "Pick drug"
    override val commonPick = "Pick"
    override val commonBaht = "THB"
    override val importsFormPickDrugPlaceholder = "Pick drug…"
    override val importsFormPickDrugTitle = "Pick drug"
    override val importsFormPickDrugSearchPlaceholder = "Search name / barcode"
    override val importsFormHeaderLotNumber = "Lot number"
    override val importsFormHeaderLotNumberPlaceholder = "e.g. A12345"
    override val importsFormHeaderExpiry = "Expiry date"
    override val importsExpiryDateLabel = "Expiry date"
    override val importsFormHeaderCostPrice = "Cost price"
    override val importsFormHeaderSellPrice = "Sell price"
    override val importsFormHeaderOptions = "Options"
    override val importsFormReceivedAll = "Received all"
    override val importsFormEditTitle = "Edit goods receipt"
    override val importsNewTitle = "New goods receipt"
    override val importsFormReceivedBadge = "Received"
    override val importsFormReceivedConfirmedHint = "This doc is confirmed — cannot be edited"
    override val importsConfirmReceiveTitle = "Confirm receipt?"
    override val importsConfirmReceiveSubtitle = "Confirm receiving the items in this import into stock — lot records will be saved and cannot be reverted"
    override val importsConfirmReceiveMessage = "Once confirmed, the system will add lots + update stock + log KY-9 — cannot be undone"
    override val importsConfirmReceiveCta = "Confirm receipt"
    override val importsConfirmDeleteDraftTitle = "Delete import?"
    override val importsConfirmDeleteDraftMessage = "This doc is not yet confirmed — deleting it cannot be undone"
    override val importsConfirmDeleteReceivedTitle = "Delete goods receipt?"
    override val importsConfirmDeleteReceivedMessage = "Delete this import? — only unconfirmed docs can be deleted"
    override val importsHeaderInvoiceNo = "Invoice no."
    override val importsHeaderInvoicePlaceholder = "Invoice / packing slip"
    override val reportsSubtitle = "Sales / stock / top sellers overview"
    override val reportsTabSummary = "Summary"
    override val reportsTabProfit = "Profit"
    override val reportsTabEod = "EOD close"
    override val reportsMetricSalesToday = "Sales today"
    override val reportsMetricSalesMonth = "Sales this month"
    override val reportsMetricProfitMonthApprox = "Profit this month (est.)"
    override val reportsMetricProfitMonthHint = "Estimated ~30% of sales"
    override val reportsMetricStockValue = "Stock value"
    override val reportsMetricStockHint: (Int, Int) -> String = { out, low -> "Out $out / Low $low" }
    override val reportsRangeToday = "Today"
    override val reportsRangeThisWeek = "This week"
    override val reportsRangeThisMonth = "This month"
    override val reportsRangeLastMonth = "Last month"
    override val reportsEmptyDay = "No data for today yet"
    override val reportsEmptyNoBills = "No bills yet"
    override val reportsEmptyNoData = "No data"
    override val reportsEmptyChartHint = "Try a different date to see sales"
    override val reportsSectionDailySales = "Daily sales"
    override val reportsSectionDailySalesEmpty = "No sales in this range"
    override val reportsSectionTopBills = "Latest bills"
    override val reportsSectionMonthly = "Revenue vs cost — monthly"
    override val reportsAvgPerDay: (String) -> String = { avg -> "Avg $avg/day" }
    override val reportsHeaderQtySold = "Qty sold"
    override val reportsHeaderBills = "Bills"
    override val reportsHeaderRevenue = "Revenue"
    override val reportsHeaderCost = "Cost"
    override val reportsHeaderProfit = "Profit"
    override val reportsHeaderDrugName = "Drug name"
    override val reportsTotalLabel = "Total"
    override val reportsRevenueVsCostLabel = "Full day"
    override val reportsProfitTitle = "Profit per drug"
    override val reportsProfitSubtitle = "Profit broken down by drug in the selected range"
    override val reportsProfitTotal = "Total profit"
    override val reportsProfitBeforeCost = "Before cost"
    override val reportsProfitRevenue = "Total revenue"
    override val reportsProfitCost = "Total cost"
    override val reportsTopSellingTitle = "Top 10 best sellers"
    override val reportsProfitTopSelling = "Top sellers"
    override val reportsProfitHighMargin = "High margin"
    override val reportsProfitNoMovement = "Stagnant items"
    override val reportsProfitSetCostHint = "Set cost price"
    override val reportsProfitMissingCostBanner: (Int) -> String = { count -> "$count items have revenue but no cost — profit may be off; click \"Set cost price\" in the stock page" }
    override val reportsProfitLossExample = "Loss example"
    override val reportsRevenueMinusCost = "Revenue - Cost"
    override val reportsCostBasis = "From cut lots"
    override val reportsDrugWord = "drug"
    override val reportsSortBy = "Sort by"
    override val reportsDatePlaceholder = "YYYY-MM-DD (blank = today)"
    override val reportsEodTitle = "End-of-day close"
    override val reportsEodSubtitle = "Sales / discount / cash summary — verify before closing"
    override val reportsEodDate = "Date"
    override val reportsEodToday = "Today"
    override val reportsEodConfirmTitle = "Confirm EOD"
    override val reportsEodConfirmMessage = "Verify totals before confirming — once closed, this cannot be reverted"
    override val reportsEodCloseCta = "Close day"
    override val reportsEodClosedBadge = "Closed"
    override val reportsEodTryDifferentRange = "Try a different date range above"
    override val reportsEodTryAnotherDate = "Try a different date to see sales"
    override val reportsEodPrintCta = "Print"
    override val reportsEodChannelSum = "All channels"
    override val reportsEodDayTotal = "Day total"
    override val reportsEodNetSalesLabel = "Net sales"
    override val reportsEodTotalDiscount = "Total discount"
    override val reportsEodCashIn = "Cash in drawer"
    override val reportsEodCashReceived = "Cash received"
    override val reportsEodChangeOut = "Change out"
    override val reportsEodReceiveMinusChange = "Received − change"
    override val reportsEodDrawerMatches = "Drawer matches sales"
    override val reportsEodDrawerMismatches = "Drawer differs from sales"
    override val reportsEodNetSalesLine: (String, Int) -> String = { net, bills -> "Net sales $net · $bills bills" }
    override val reportsEodNetSalesAndCashLine: (String, String) -> String = { cash, sales -> "Cash in drawer $cash · sales $sales" }
    override val reportsEodCashLine: (String) -> String = { cash -> "Cash in drawer $cash" }
    override val reportsEodClosedDate: (String) -> String = { date -> "EOD closed — $date" }
    override val reportsBillsOfDay: (Int) -> String = { count -> "Bills for the day · $count items" }
    override val reportsWalkInCustomer = "Walk-in"
}

val LocalPharmStrings = staticCompositionLocalOf<PharmStrings> { PharmStringsTh }

val pharmStrings: PharmStrings
    @Composable
    @ReadOnlyComposable
    get() = LocalPharmStrings.current
