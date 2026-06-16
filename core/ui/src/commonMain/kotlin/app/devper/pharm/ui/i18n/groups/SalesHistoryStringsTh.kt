package app.devper.pharm.ui.i18n.groups

object SalesHistoryStringsTh : SalesHistoryStrings {
    override val salesHistoryReturnTitle: (String) -> String = { bill -> "คืนสินค้าจากบิล $bill" }
    override val salesHistoryReturnSubtitle = "เลือกจำนวนที่จะคืน — ระบบจะคำนวณยอดคืนให้เอง"
    override val salesHistoryReasonLabel = "เหตุผล"
    override val salesHistoryReturnReasonPlaceholder = "เหตุผลการคืน เช่น ลูกค้าเปลี่ยนใจ, สินค้าเสีย ฯลฯ"
    override val salesHistoryReturnConfirmCta = "ยืนยันคืนสินค้า"
    override val salesHistorySoldRemaining: (Int, String, Int) -> String = { q, u, r -> "ขายไป $q $u · เหลือคืน $r" }
    override val salesHistoryRefund: (String) -> String = { v -> "คืนเงิน $v" }
    override val salesHistoryBillTitle: (String) -> String = { bill -> "บิล $bill" }
    override val salesHistoryVoidedBadge = "ยกเลิกแล้ว"
    override val salesHistoryReturnedQty: (Int) -> String = { n -> "คืนแล้ว $n" }
    override val salesHistoryTotalRow = "รวม"
    override val salesHistoryDiscountRow = "ส่วนลด"
    override val salesHistoryNetRow = "สุทธิ"

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
    override val salesHistoryLoadBillsFailed = "โหลดรายการบิลไม่สำเร็จ"
    override val salesHistoryLoadItemsFailed = "โหลดรายการสินค้าไม่สำเร็จ"
    override val salesHistorySubmitReturnFailed = "บันทึกการคืนสินค้าไม่สำเร็จ"
    override val salesHistoryMetricNetSales = "ยอดขายสุทธิ"
    override val salesHistoryMetricBills = "จำนวนบิล"
    override val salesHistoryMetricAvg = "เฉลี่ย/บิล"
    override val salesHistoryMetricVoided = "ยกเลิก"
    override val salesHistoryRangeToday = "วันนี้"
    override val salesHistoryRange7d = "7 วัน"
    override val salesHistoryRangeMonth = "เดือนนี้"
    override val salesHistoryReturnReasonRequired = "กรุณาระบุเหตุผลการคืนสินค้า"
    override val salesHistoryReturnItemsRequired = "กรุณาเลือกอย่างน้อย 1 รายการที่จะคืน"
}
