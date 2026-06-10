package app.devper.pharm.ui.i18n.groups

object BulkImportStringsTh : BulkImportStrings {
    override val bulkImportReadyBadge = "พร้อมนำเข้า"

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
    override val bulkImportPickFileFailed = "เลือกไฟล์ไม่สำเร็จ"
    override val bulkImportImportFailed = "นำเข้าไม่สำเร็จ"
    override val bulkImportNoRows = "ไม่มีรายการให้นำเข้า"
    override val bulkImportInvalidJson = "รูปแบบ JSON ไม่ถูกต้อง"
    override val bulkImportPasteFirst = "วาง JSON ก่อนตรวจสอบ"
    override val bulkImportNotArray = "ต้องเป็น array หรือ {drugs: [...]}"
    override val bulkImportRowNotObject: (Int) -> String = { row -> "รายการที่ $row: ต้องเป็น JSON object" }
    override val bulkImportRowMissingName = "ทุกแถวต้องมีฟิลด์ name"
}
