package app.devper.pharm.ui.i18n.groups

object MovementsStringsTh : MovementsStrings {
    override val movementsSpecImport = "นำเข้า"
    override val movementsSpecSale = "ขาย"
    override val movementsSpecReturn = "คืนยา"
    override val movementsSpecAdjustment = "ปรับสต็อก"
    override val movementsSpecWriteoff = "ตัดสต็อก"
    override val movementsSpecVoided = "ยกเลิกบิล"

    override val movementsSubtitle = "ประวัติการเข้า-ออกของสต็อก"
    override val movementsSearchPlaceholder = "ค้นหาชื่อยา…"
    override val movementsCountNoun = "รายการ"
    override val movementsEmpty = "ไม่มีรายการในช่วงเวลานี้"
    override val movementsEmptySearching = "ไม่พบความเคลื่อนไหวที่ตรงกับตัวกรอง"
    override val movementsHeaderType = "ประเภท"
    override val movementsHeaderRef = "อ้างอิง"
    override val movementsHeaderBy = "โดย"
    override val movementsPrevPage = "‹ ก่อนหน้า"
    override val movementsNextPage = "ถัดไป ›"
    override val movementsPagination: (Int, Int) -> String = { page, total -> "หน้า $page / $total" }
    override val movementsShownOf: (Int, Int) -> String = { shown, total -> "แสดง $shown จาก $total รายการ" }
    override val movementsLoadHistoryFailed = "โหลดประวัติไม่สำเร็จ"

    override val movementsCsvHeaderAt = "เวลา"
    override val movementsCsvHeaderType = "ประเภท"
    override val movementsCsvHeaderDrug = "ยา"
    override val movementsCsvHeaderQty = "จำนวน"
    override val movementsCsvHeaderRef = "อ้างอิง"
    override val movementsCsvHeaderNote = "หมายเหตุ"
}
