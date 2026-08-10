package app.devper.pharm.ui.i18n.groups

object ExpiryStringsTh : ExpiryStrings {
    override val expiryLoadLotsFailed = "โหลดล็อตใกล้หมดอายุไม่สำเร็จ"
    override val expiryWriteoffFailures: (Int) -> String = { n -> "$n ล็อตล้มเหลว — กรุณาลองใหม่" }
    override val expiryMoreFailures: (Int) -> String = { n -> "(+$n อื่นๆ)" }

    override val expiryWindow30 = "30 วัน"
    override val expiryWindow60 = "60 วัน"
    override val expiryWindow90 = "90 วัน"
    override val expiryWindow180 = "180 วัน"
    override val expiryWindowExpired = "หมดอายุแล้ว"

    override val expirySubtitle = "ตรวจล็อตใกล้หมดอายุ และตัดจำหน่าย"
    override val expirySearchPlaceholder = "ค้นหาชื่อยา หรือเลขล็อต…"
    override val expirySearchNotFound = "ไม่พบล็อตตามที่ค้นหา"
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
        "ระบบจะลบ $count ล็อต และหักสต็อกตามจำนวนคงเหลือของแต่ละล็อต — " +
        "บันทึกการตัดจำหน่ายไว้สำหรับตรวจสอบ"
    }
    override val expiryResultSuccessTitle = "ตัดจำหน่ายสำเร็จ"
    override val expiryResultPartialTitle = "ตัดจำหน่ายบางส่วน"
    override val expiryResultSummary: (Int, Int) -> String = { writtenOff, total -> "บันทึก $writtenOff/$total ล็อต" }
    override val expiryWriteoffFailed = "ตัดจำหน่ายไม่สำเร็จ"
}
