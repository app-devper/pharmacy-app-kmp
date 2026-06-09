package app.devper.pharm.ui.i18n.groups

object MovementsStringsTh : MovementsStrings {
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
}
