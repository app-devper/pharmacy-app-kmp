package app.devper.pharm.ui.i18n.groups

object PlanningStringsTh : PlanningStrings {
    override val planningLoadLowStockFailed = "โหลดยาใกล้หมดไม่สำเร็จ"
    override val planningLoadReorderFailed = "โหลดคำแนะนำสั่งซื้อไม่สำเร็จ"
    override val planningTitle = "คำแนะนำสั่งซื้อ"
    override val planningRefreshCta = "รีเฟรช"
    override val planningAddPoCta = "เพิ่มใบสั่งซื้อ"
    override val planningAddAllCta = "เพิ่มทั้งหมด"
    override val planningAddRemainingCta: (Int) -> String = { count -> "เพิ่มที่เหลือ ($count)" }
    override val planningAddedBadge = "เพิ่มแล้ว"
    override val planningAddedMessage: (Int) -> String = { count -> "เพิ่มในใบสั่งซื้อแล้ว $count รายการ" }
    override val planningDismissCta = "ซ่อนคำแนะนำ"
    override val planningOpenPoCta: (Int) -> String = { n -> "สร้างใบสั่งซื้อ ($n)" }
    override val planningLowStockTitle = "ยาใกล้หมด"
    override val planningBelowMinTitle = "ยาที่ต่ำกว่าระดับสต็อกขั้นต่ำ"
    override val planningLowStockSearchPlaceholder = "ค้นหาชื่อยา ชื่อสามัญ หรือบาร์โค้ด…"
    override val planningLowStockNotFound = "ไม่พบยาใกล้หมดตามที่ค้นหา"
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
    override val planningTrackStockFailed = "ติดตามการเปลี่ยนแปลงสต็อกไม่สำเร็จ"
}
