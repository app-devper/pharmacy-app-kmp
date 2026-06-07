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
}

val LocalPharmStrings = staticCompositionLocalOf<PharmStrings> { PharmStringsTh }

val pharmStrings: PharmStrings
    @Composable
    @ReadOnlyComposable
    get() = LocalPharmStrings.current
