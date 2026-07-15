package app.devper.pharm.ui.i18n.groups

object LabelsStringsTh : LabelsStrings {
    override val labelsAddCta = "+ เพิ่ม"

    override val labelsSubtitle = "ออกแบบและพิมพ์ฉลากยา"
    override val labelsSearchPlaceholder = "ค้นหายา (เพิ่มทีละบรรทัด)…"
    override val labelsNoDrugs = "ยังไม่มียาให้เลือก"
    override val labelsNoSearchResults = "ไม่พบยาที่ค้นหา"
    override val labelsEmpty = "ยังไม่มีรายการ เลือกยาทางซ้ายเพื่อเพิ่ม"
    override val labelsListTitle: (Int) -> String = { count -> "รายการฉลาก ($count บรรทัด)" }
    override val labelsRemoveLine = "ลบบรรทัด"
    override val labelsClear = "ล้าง"
    override val labelsClearTitle = "ล้างรายการฉลาก?"
    override val labelsClearSubtitle = "รายการฉลากที่เตรียมไว้ทั้งหมดจะถูกนำออก"
    override val labelsClearConfirm = "ล้างรายการ"
    override val labelsSizeLabel = "ขนาดฉลาก"
    override val labelsSizeSmall = "38 × 25 มม."
    override val labelsSizeMedium = "50 × 30 มม."
    override val labelsPreviewLabel: (String) -> String = { size -> "ตัวอย่าง ($size)" }
    override val labelsPrintCount: (Int) -> String = { count -> "พิมพ์ $count ดวง" }
    override val labelsPrinting = "กำลังพิมพ์…"
    override val labelsPrintSuccess = "พิมพ์สำเร็จ"
    override val labelsTotalPrice = "รวมราคา"
    override val labelsLotPrefix = "ล็อต"
    override val labelsLotUnspecified = "(ไม่ระบุ)"
    override val labelsPrintFailed = "พิมพ์ไม่สำเร็จ"
}
