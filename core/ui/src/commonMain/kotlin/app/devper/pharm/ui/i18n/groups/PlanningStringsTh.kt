package app.devper.pharm.ui.i18n.groups

object PlanningStringsTh : PlanningStrings {
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
}
